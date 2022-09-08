package fr.fabienhebuterne.pickspawner.command

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.command.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.command.factory.CommandInfo
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
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

const val GIVE_COMMAND_USAGE = "/pickspawner give <player|uuid> <spawner|custom_pickaxe> <entity>"

@CommandInfo("give", "pickspawner.give", 2, GIVE_COMMAND_USAGE)
class GiveCommand(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : AbstractCommand() {

    override var literalArgumentBuilder: LiteralArgumentBuilder<String> =
        LiteralArgumentBuilder.literal<String?>("give")
            .then(
                RequiredArgumentBuilder.argument<String?, String?>("playerNameOrUuid", StringArgumentType.word())
                    .then(LiteralArgumentBuilder.literal<String>("spawner").entityTypesToCommodore())
                    .then(
                        LiteralArgumentBuilder.literal<String?>("custom_pickaxe").then(
                            LiteralArgumentBuilder.literal("damage")
                        )
                    )
                    .build()
            )

    private fun LiteralArgumentBuilder<String>.entityTypesToCommodore(): LiteralArgumentBuilder<String> {
        return EntityType.values().fold(this) { accumulator, entity ->
            accumulator.then(LiteralArgumentBuilder.literal(entity.name))
        }
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
            "SPAWNER" -> giveSpawner(args, player)
            "CUSTOM_PICKAXE" -> giveCustomPickaxe(args, player)
            else -> throw BadArgumentException(commandSender, GIVE_COMMAND_USAGE)
        }
    }

    private fun giveCustomPickaxe(
        args: Array<out String>,
        player: Player
    ) {
        val damage = args.getOrNull(2)?.toIntOrNull() ?: 0
        val result = player.inventory.addItem(itemInitService.initCustomPickaxeItemStack(damage))
        if(!result.isEmpty())
        {
            result.forEach { (_: Int, itemStack: ItemStack) ->
                player.world.dropItem(player.location, itemStack);
            }

            player.sendMessage(instance.translationConfig.errors.missingPlaceInventoryGiveDrop.toColorHex())
        }
    }

    private fun giveSpawner(
        args: Array<out String>,
        player: Player
    ) {
        val entityType = EntityType.valueOf(args[2].uppercase())
        val result = player.inventory.addItem(itemInitService.initSpawnerItemStack(entityType))
        if(!result.isEmpty())
        {
            result.forEach { (_: Int, itemStack: ItemStack) ->
                player.world.dropItem(player.location, itemStack);
            }

            player.sendMessage(instance.translationConfig.errors.missingPlaceInventoryGiveDrop.toColorHex())
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