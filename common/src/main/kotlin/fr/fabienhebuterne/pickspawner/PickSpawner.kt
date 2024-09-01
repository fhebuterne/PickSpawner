package fr.fabienhebuterne.pickspawner

import fr.fabienhebuterne.pickspawner.command.CommandsRegistration
import fr.fabienhebuterne.pickspawner.config.*
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import fr.fabienhebuterne.pickspawner.module.breakspawner.*
import fr.fabienhebuterne.pickspawner.module.cancelenchant.EnchantItemEventListener
import fr.fabienhebuterne.pickspawner.module.cancelrepair.PlayerCommandPreprocessListener
import fr.fabienhebuterne.pickspawner.module.cancelrepair.PrepareAnvilEventListener
import fr.fabienhebuterne.pickspawner.module.commodore.CommodoreService
import fr.fabienhebuterne.pickspawner.module.entitydamage.EntityDamageByEntityEventListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteractEventListener
import fr.fabienhebuterne.pickspawner.module.pickaxeMigration.PickaxeMigrationPlayerInteractEventListener
import fr.fabienhebuterne.pickspawner.module.placespawner.BlockPlaceEventListener
import fr.fabienhebuterne.pickspawner.nms.*
import me.lucko.commodore.CommodoreProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class PickSpawner : JavaPlugin() {

    private var economy: Economy? = null
    lateinit var nms: Utils
    lateinit var defaultConfig: DefaultConfig
    lateinit var translationConfig: TranslationConfig
    lateinit var migrationPickaxeConfig: MigrationPickaxeConfig

    override fun onEnable() {
        setupEconomy()
        loadNms()
        loadConfigs()
        val itemInitService = ItemInitService(this)
        loadListeners(itemInitService)
        registerCommands(itemInitService)
    }

    override fun onDisable() {}

    private fun loadNms() {
        val currentVersion: String? =
            Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
                .getOrNull(3)

        val minecraftVersion: String = Bukkit.getServer().bukkitVersion

        nms = when (currentVersion) {
            "v1_18_R2" -> Utils_1_18_R2()
            "v1_19_R1" -> Utils_1_19_R1()
            "v1_19_R2" -> Utils_1_19_R2()
            "v1_19_R3" -> Utils_1_19_R3()
            "v1_20_R1" -> Utils_1_20_R1()
            "v1_20_R2" -> Utils_1_20_R2()
            "v1_20_R3" -> Utils_1_20_R3()
            else -> {
                // checking version in class name has changed since 1.20.6
                when (minecraftVersion) {
                    "1.20.6-R0.1-SNAPSHOT" -> Utils_1_20_R4()
                    "1.21-R0.1-SNAPSHOT" -> Utils_1_21_R1()
                    "1.21.1-R0.1-SNAPSHOT" -> Utils_1_21_R1()
                    else -> {
                        Bukkit.getLogger().severe("Your server version $currentVersion / $minecraftVersion isn't compatible with PickSpawner")
                        server.pluginManager.disablePlugin(this)
                        throw IllegalStateException("Your server version $currentVersion / $minecraftVersion isn't compatible with PickSpawner")
                    }
                }
            }
        }
    }

    fun loadConfigs() {
        val defaultConfigService = DefaultConfigService(this)
        defaultConfigService.createAndLoadConfig(true)
        defaultConfig = defaultConfigService.getSerialization()

        if (!defaultConfig.spawnersStackable) {
            nms.setMaxStackSize(1)
        }

        val translationConfigService = TranslationConfigService(this)
        translationConfigService.createAndLoadConfig(true)
        translationConfig = translationConfigService.getSerialization()

        val migrationPickaxeConfigService = MigrationPickaxeConfigService(this)
        migrationPickaxeConfig = migrationPickaxeConfigService.loadAndFindConfigIfExist() ?: MigrationPickaxeConfig()
    }

    private fun loadListeners(itemInitService: ItemInitService) {
        val pluginManager = server.pluginManager
        val spawnerItemStackService = SpawnerItemStackService(this, itemInitService)
        pluginManager.registerEvents(
            BlockBreakEventListener(
                this,
                SilkTouchPickaxeService(this, spawnerItemStackService),
                CustomPickaxeService(this, spawnerItemStackService),
                spawnerItemStackService
            ), this
        )
        pluginManager.registerEvents(PrepareAnvilEventListener(this), this)
        pluginManager.registerEvents(EnchantItemEventListener(this), this)
        pluginManager.registerEvents(PlayerInteractEventListener(this), this)
        pluginManager.registerEvents(EntityDamageByEntityEventListener(this), this)
        pluginManager.registerEvents(PickaxeMigrationPlayerInteractEventListener(this, itemInitService), this)
        pluginManager.registerEvents(PlayerItemDamageListener(this, itemInitService), this)
        pluginManager.registerEvents(PlayerCommandPreprocessListener(this), this)
        pluginManager.registerEvents(BlockPlaceEventListener(this), this)
    }

    private fun registerCommands(itemInitService: ItemInitService) {
        val baseCommand = "pickspawner"
        val commandsRegistration = CommandsRegistration(this, itemInitService, baseCommand)

        // Register base command
        val command = getCommand(baseCommand)
        command?.setExecutor(commandsRegistration)

        // Register commodore
        if (CommodoreProvider.isSupported()) {
            CommodoreService().init(this, commandsRegistration, command)
        }
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

    fun getEconomy(): Economy {
        return economy ?: throw IllegalStateException("Economy isn't available")
    }

}
