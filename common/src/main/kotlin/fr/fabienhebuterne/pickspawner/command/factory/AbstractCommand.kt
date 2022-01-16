package fr.fabienhebuterne.pickspawner.command.factory

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractCommand {
    open lateinit var literalArgumentBuilder: LiteralArgumentBuilder<String>
    // Command executed by player
    open fun runOnPlayer(
        playerSender: Player,
        command: Command,
        label: String,
        args: Array<out String>
    ) {
        throw NotImplementedError("This command can't be executed by a player, try in the console")
    }

    // Command executed by Console, CommandBlock, etc...
    open fun runOnOther(
        commandSender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) {
        throw NotImplementedError("This command can't be executed by the console, try with a player")
    }
}
