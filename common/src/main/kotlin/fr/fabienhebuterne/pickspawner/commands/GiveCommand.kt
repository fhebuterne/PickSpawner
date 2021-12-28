package fr.fabienhebuterne.pickspawner.commands

import fr.fabienhebuterne.pickspawner.commands.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.commands.factory.CommandInfo
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandInfo("give", "pickspawner.give", 2, "/pickspawner give <player> <spawner|pickaxe> <entity|pickaxe_type>")
class GiveCommand : AbstractCommand() {

    override fun runOnPlayer(player: Player, command: Command, label: String, args: Array<out String>) {

    }

    override fun runOnOther(sender: CommandSender, command: Command, label: String, args: Array<out String>) {

    }

}
