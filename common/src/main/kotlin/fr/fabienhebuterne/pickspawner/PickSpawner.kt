package fr.fabienhebuterne.pickspawner

import fr.fabienhebuterne.pickspawner.config.DefaultConfig
import fr.fabienhebuterne.pickspawner.config.DefaultConfigService
import fr.fabienhebuterne.pickspawner.config.TranslationConfig
import fr.fabienhebuterne.pickspawner.config.TranslationConfigService
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.breakspawner.BlockBreakEventListener
import fr.fabienhebuterne.pickspawner.module.breakspawner.CustomPickaxeService
import fr.fabienhebuterne.pickspawner.module.breakspawner.SilkTouchPickaxeService
import fr.fabienhebuterne.pickspawner.module.breakspawner.SpawnerItemStackService
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin


class PickSpawner : JavaPlugin() {

    private var economy: Economy? = null
    lateinit var defaultConfig: DefaultConfig
    lateinit var translationConfig: TranslationConfig

    override fun onEnable() {
        setupEconomy()
        loadConfig()
        loadListener()
    }

    override fun onDisable() {}

    private fun loadConfig() {
        val defaultConfigService = DefaultConfigService(this)
        defaultConfigService.createAndLoadConfig(true)
        defaultConfig = defaultConfigService.getSerialization()

        val translationConfigService = TranslationConfigService(this)
        translationConfigService.createAndLoadConfig(true)
        translationConfig = translationConfigService.getSerialization()
    }

    private fun loadListener() {
        val spawnerItemStackService = SpawnerItemStackService(this)
        registerEvent(
            BlockBreakEvent::class.java,
            BlockBreakEventListener(
                this,
                SilkTouchPickaxeService(this, spawnerItemStackService),
                CustomPickaxeService(this, spawnerItemStackService)
            )
        )
    }

    // We need to use registerEvent with more parameters because we use generic abstract class to init try catch
    private fun registerEvent(eventClass: Class<out Event>, listener: BaseListener<*>) {
        server.pluginManager.registerEvent(
            eventClass,
            listener,
            EventPriority.NORMAL,
            listener,
            this
        )
    }

    private fun setupEconomy(): Boolean {
        val economyProvider = server.servicesManager
            .getRegistration(Economy::class.java)
        if (economyProvider != null) {
            economy = economyProvider.provider
            logger.info("Loaded with economy plugin ${economy?.name}")
        } else {
            logger.warning("Can't found any economy plugin with Vault")
        }
        return economy != null
    }


}
