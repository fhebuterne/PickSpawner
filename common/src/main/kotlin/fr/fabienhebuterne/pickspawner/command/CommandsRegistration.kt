package fr.fabienhebuterne.pickspawner.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.LiteralCommandNode
import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.command.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.command.factory.CommandInfoInit
import fr.fabienhebuterne.pickspawner.exception.CustomException
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


typealias CommandName = String

class CommandsRegistration(
    private val instance: PickSpawner,
    itemInitService: ItemInitService,
    private val pickSpawnerBaseCommodore: LiteralArgumentBuilder<String>
) : CommandExecutor {

    private val commands: MutableMap<CommandName, CommandInfoInit> = mutableMapOf()

    init {
        registration(
            BuyCommand(instance, itemInitService),
            GiveCommand(itemInitService),
            ReloadCommand(instance)
        )
    }

    fun getCommodoreBuild(): LiteralCommandNode<String>? {
        return commands
            .map { it.value }
            .fold(pickSpawnerBaseCommodore) { acc, commandInfoInit ->
                acc.then(commandInfoInit.abstractCommand.literalArgumentBuilder)
            }
            .build()
    }

    fun hasCommodorePermission(commandSender: CommandSender): Boolean {
        return commands.any { entry -> commandSender.hasPermission(entry.value.permission) }
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
        if (args.isEmpty()) {
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
        val filterArgs = args.drop(1).toTypedArray()

        try {
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
        } catch (ignored: CustomException) {
            // We ignore CustomException because error msg send to player
            // and don't want to have stacktrace on console
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }


}
