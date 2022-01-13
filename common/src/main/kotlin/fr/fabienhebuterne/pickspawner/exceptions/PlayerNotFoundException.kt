package fr.fabienhebuterne.pickspawner.exceptions

import org.bukkit.command.CommandSender

class PlayerNotFoundException(
    commandSender: CommandSender,
    playerOrUuidNotFound: String
) : CustomException() {

    init {
        commandSender.sendMessage("$playerOrUuidNotFound cannot be found")
    }

}