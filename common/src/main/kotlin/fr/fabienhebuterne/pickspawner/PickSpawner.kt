package fr.fabienhebuterne.pickspawner

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import fr.fabienhebuterne.pickspawner.command.CommandsRegistration
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
import fr.fabienhebuterne.pickspawner.module.entitydamage.EntityDamageByEntityEventListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteractEventListener
import fr.fabienhebuterne.pickspawner.nms.Utils
import fr.fabienhebuterne.pickspawner.nms.Utils_1_18_R2
import fr.fabienhebuterne.pickspawner.nms.Utils_1_19_R1
import me.lucko.commodore.CommodoreProvider
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class PickSpawner : JavaPlugin() {

    private var economy: Economy? = null
    lateinit var nms: Utils
    lateinit var defaultConfig: DefaultConfig
    lateinit var translationConfig: TranslationConfig

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
        var currentVersion: String? = null
        try {
            currentVersion = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()[3]
        } catch (_: ArrayIndexOutOfBoundsException) {
        }

        if (currentVersion == null) {
            Bukkit.getLogger().severe("Your server version isn't compatible with PickSpawner")
            server.pluginManager.disablePlugin(this)
        }

        nms = when (currentVersion) {
            "v1_18_R2" -> Utils_1_18_R2()
            "v1_19_R1" -> Utils_1_19_R1()
            else -> {
                Bukkit.getLogger().severe("Your server version isn't compatible with PickSpawner")
                server.pluginManager.disablePlugin(this)
                throw IllegalStateException("Your server version isn't compatible with PickSpawner")
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
        registerEvent(EntityDamageByEntityEvent::class.java, EntityDamageByEntityEventListener(this))
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
