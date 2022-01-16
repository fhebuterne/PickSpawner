package fr.fabienhebuterne.pickspawner.exception

import org.bukkit.command.CommandSender

class BadArgumentException(
    commandSender: CommandSender,
    commandUsage: String
) : CustomException() {

    init {
        commandSender.sendMessage("Usage : $commandUsage")
    }

}