package fr.fabienhebuterne.pickspawner.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.command.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.command.factory.CommandInfo
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

const val RELOAD_COMMAND_USAGE = "/pickspawner reload"

@CommandInfo("reload", "pickspawner.reload", 1, RELOAD_COMMAND_USAGE)
class ReloadCommand(
    private val instance: PickSpawner
) : AbstractCommand() {

    override var literalArgumentBuilder: LiteralArgumentBuilder<String> =
        LiteralArgumentBuilder.literal("reload")

    override fun runOnPlayer(playerSender: Player, command: Command, label: String, args: Array<out String>) {
        reload(playerSender)
    }

    override fun runOnOther(commandSender: CommandSender, command: Command, label: String, args: Array<out String>) {
        reload(commandSender)
    }

    private fun reload(commandSender: CommandSender) {
        commandSender.sendMessage(instance.translationConfig.reloadInProgress.toColorHex())
        instance.loadConfigs()
        commandSender.sendMessage(instance.translationConfig.reloadEnded.toColorHex())
    }

}