package fr.fabienhebuterne.pickspawner.exception

import org.bukkit.command.CommandSender

class PlayerNotFoundException(
    commandSender: CommandSender,
    playerOrUuidNotFound: String
) : CustomException() {

    init {
        commandSender.sendMessage("$playerOrUuidNotFound cannot be found")
    }

}