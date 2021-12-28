package fr.fabienhebuterne.pickspawner.commands.factory

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class AbstractCommand {
    // Command executed by player
    open fun runOnPlayer(
        player: Player,
        command: Command,
        label: String,
        args: Array<out String>
    ) {
        throw NotImplementedError("This command can't be executed by a player, try in the console")
    }

    // Command executed by Console, CommandBlock, etc...
    open fun runOnOther(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) {
        throw NotImplementedError("This command can't be executed by the console, try with a player")
    }
}
