package fr.fabienhebuterne.pickspawner.commands

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.commands.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.commands.factory.CommandInfoInit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias CommandName = String

class CommandsRegistration(private val instance: PickSpawner): CommandExecutor {

    private val commands: MutableMap<CommandName, CommandInfoInit> = mutableMapOf()

    init {
        registration(
            GiveCommand()
        )
    }

    private fun registration(vararg abstractCommand: AbstractCommand) {
        abstractCommand.forEach {
            val commandInfos = CommandInfoInit(it)
            commands[commandInfos.name] = commandInfos
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.size <= 1) {
            return false
        }

        val commandArgs: String = args[0]

        // return false can indicate to sender command not found
        val commandInfoInit = commands[commandArgs] ?: return false
        val abstractCommand = commandInfoInit.abstractCommand

        if (args.size < commandInfoInit.minimalRequiredArgs) {
            sender.sendMessage(commandInfoInit.usage)
            return false
        }

        // we drop first arg because only used to execute sub command
        val filterArgs = args.drop(0).toTypedArray()

        if (sender is Player) {
            if (!sender.hasPermission(commandInfoInit.permission)) {
                sender.sendMessage(instance.translationConfig.errors.missingPermission)
                return true
            }

            abstractCommand.runOnPlayer(
                sender, command, label, filterArgs
            )
        } else {
            abstractCommand.runOnOther(
                sender, command, label, filterArgs
            )
        }

        return true
    }



}
