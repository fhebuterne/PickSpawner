package fr.fabienhebuterne.pickspawner.exceptions

import org.bukkit.command.CommandSender

class BadArgumentException(
    commandSender: CommandSender,
    commandUsage: String
) : CustomException() {

    init {
        commandSender.sendMessage("Usage : $commandUsage")
    }

}