package fr.fabienhebuterne.pickspawner.module.commodore

import fr.fabienhebuterne.pickspawner.command.CommandsRegistration
import me.lucko.commodore.CommodoreProvider
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin

class CommodoreService {

    fun init(plugin: JavaPlugin, commandsRegistration: CommandsRegistration, command: PluginCommand?) {
        val commodoreBuild = commandsRegistration.getCommodoreBuild()
        val commodore = CommodoreProvider.getCommodore(plugin)
        commodore.register(command, commodoreBuild) { commandsRegistration.hasCommodorePermission(it) }
    }

}