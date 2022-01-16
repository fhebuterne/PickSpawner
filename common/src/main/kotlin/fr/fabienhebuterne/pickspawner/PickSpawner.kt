package fr.fabienhebuterne.pickspawner

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import fr.fabienhebuterne.pickspawner.commands.CommandsRegistration
import fr.fabienhebuterne.pickspawner.config.DefaultConfig
import fr.fabienhebuterne.pickspawner.config.DefaultConfigService
import fr.fabienhebuterne.pickspawner.config.TranslationConfig
import fr.fabienhebuterne.pickspawner.config.TranslationConfigService
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import fr.fabienhebuterne.pickspawner.module.breakspawner.BlockBreakEventListener
import fr.fabienhebuterne.pickspawner.module.breakspawner.CustomPickaxeService
import fr.fabienhebuterne.pickspawner.module.breakspawner.SilkTouchPickaxeService
import fr.fabienhebuterne.pickspawner.module.breakspawner.SpawnerItemStackService
import fr.fabienhebuterne.pickspawner.module.cancelenchant.EnchantItemEventListener
import fr.fabienhebuterne.pickspawner.module.cancelrepair.PrepareAnvilEventListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteractEventListener
import me.lucko.commodore.CommodoreProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class PickSpawner : JavaPlugin() {

    private var economy: Economy? = null
    lateinit var defaultConfig: DefaultConfig
    lateinit var translationConfig: TranslationConfig

    override fun onEnable() {
        setupEconomy()
        loadConfigs()
        val itemInitService = ItemInitService(this)
        loadListeners(itemInitService)
        registerCommands(itemInitService)
    }

    override fun onDisable() {}

    private fun loadConfigs() {
        val defaultConfigService = DefaultConfigService(this)
        defaultConfigService.createAndLoadConfig(true)
        defaultConfig = defaultConfigService.getSerialization()

        val translationConfigService = TranslationConfigService(this)
        translationConfigService.createAndLoadConfig(true)
        translationConfig = translationConfigService.getSerialization()
    }

    private fun loadListeners(itemInitService: ItemInitService) {
        val spawnerItemStackService = SpawnerItemStackService(this, itemInitService)
        registerEvent(
            BlockBreakEvent::class.java,
            BlockBreakEventListener(
                this,
                SilkTouchPickaxeService(this, spawnerItemStackService),
                CustomPickaxeService(this, spawnerItemStackService)
            )
        )
        registerEvent(PrepareAnvilEvent::class.java, PrepareAnvilEventListener(this))
        registerEvent(EnchantItemEvent::class.java, EnchantItemEventListener(this))
        registerEvent(PlayerInteractEvent::class.java, PlayerInteractEventListener(this))
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

    private fun registerCommands(itemInitService: ItemInitService) {
        val baseCommand = "pickspawner"
        val pickSpawnerBaseCommodore = LiteralArgumentBuilder.literal<String>(baseCommand)
        val commandsRegistration = CommandsRegistration(this, itemInitService, pickSpawnerBaseCommodore)

        // Register base command
        val command = getCommand(baseCommand)
        command?.setExecutor(commandsRegistration)

        // Register commodore
        val commodoreBuild = commandsRegistration.getCommodoreBuild()
        val commodore = CommodoreProvider.getCommodore(this)
        commodore.register(command, commodoreBuild) { commandsRegistration.hasCommodorePermission(it) }
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
