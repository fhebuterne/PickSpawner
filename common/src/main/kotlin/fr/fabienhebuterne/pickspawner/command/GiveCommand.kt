package fr.fabienhebuterne.pickspawner.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.command.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.command.factory.CommandInfo
import fr.fabienhebuterne.pickspawner.exception.BadArgumentException
import fr.fabienhebuterne.pickspawner.exception.PlayerNotFoundException
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

const val GIVE_COMMAND_USAGE = "/pickspawner give <player|uuid> <spawner|pickaxe> <entity>"

@CommandInfo("give", "pickspawner.give", 2, GIVE_COMMAND_USAGE)
class GiveCommand(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : AbstractCommand() {

    override var literalArgumentBuilder: LiteralArgumentBuilder<String> =
        LiteralArgumentBuilder.literal<String?>("give")
            .then(
                RequiredArgumentBuilder.argument<String?, String?>("playerNameOrUuid", StringArgumentType.word())
                    .then(entityTypesToCommodore())
                    .then(LiteralArgumentBuilder.literal("pickaxe"))
                    .build()
            )

    private fun entityTypesToCommodore(): LiteralArgumentBuilder<String>? {
        var spawnerCommodore = LiteralArgumentBuilder.literal<String>("spawner")

        EntityType.values().forEach {
            spawnerCommodore = spawnerCommodore.then(LiteralArgumentBuilder.literal(it.name))
        }

        return spawnerCommodore
    }

    override fun runOnPlayer(playerSender: Player, command: Command, label: String, args: Array<out String>) {
        give(args, playerSender)
    }

    override fun runOnOther(commandSender: CommandSender, command: Command, label: String, args: Array<out String>) {
        give(args, commandSender)
    }

    private fun give(args: Array<out String>, commandSender: CommandSender) {
        val playerNameOrUuid = args[0]

        val player: Player = getPlayerFromArg(playerNameOrUuid)
            ?: throw PlayerNotFoundException(commandSender, playerNameOrUuid)

        when (args[1].uppercase()) {
            "SPAWNER" -> {
                val entityType = EntityType.valueOf(args[2].uppercase())
                val result = player.inventory.addItem(itemInitService.initSpawnerItemStack(entityType))
                showAddItemResult(result, player, commandSender, entityType)
            }
            "PICKAXE" -> {
                val result = player.inventory.addItem(itemInitService.initCustomPickaxeItemStack())
                showAddItemResult(result, player, commandSender)
            }
            else -> throw BadArgumentException(commandSender, GIVE_COMMAND_USAGE)
        }
    }

    private fun showAddItemResult(
        result: HashMap<Int, ItemStack>,
        player: Player,
        commandSender: CommandSender,
        entityType: EntityType? = null
    ) {
        result.forEach { (_: Int, itemStack: ItemStack) ->
            if (entityType != null) {
                Bukkit.getLogger()
                    .warning("Missing place in ${player.name} (${player.uniqueId}) inventory - can't add ${itemStack.amount} x ${itemStack.type} - $entityType")
                commandSender.sendMessage("Missing place in inventory - can't add ${itemStack.amount} x ${itemStack.type} - $entityType")
            } else {
                Bukkit.getLogger()
                    .warning("Missing place in ${player.name} (${player.uniqueId}) inventory - can't add ${itemStack.amount} x ${itemStack.type}")
                commandSender.sendMessage("Missing place in inventory - can't add ${itemStack.amount} x ${itemStack.type}")
            }
        }
    }

    private fun getPlayerFromArg(playerNameOrUuid: String): Player? {
        return if (playerNameOrUuid.length == 36) {
            val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerNameOrUuid))
            offlinePlayer.player
        } else {
            Bukkit.getPlayer(playerNameOrUuid)
        }
    }

}