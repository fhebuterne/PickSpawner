package fr.fabienhebuterne.pickspawner.module.interactspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractEventListener(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : BaseListener<PlayerInteractEvent>() {
    override fun execute(event: PlayerInteractEvent) {
        migrateOldSpawnerToNewSpawner(event)
        cancelUpdateSpawnerWithEggs(event)
    }

    private fun migrateOldSpawnerToNewSpawner(event: PlayerInteractEvent) {
        val itemStack = event.item

        if (itemStack == null || itemStack.type != Material.SPAWNER) {
            return
        }

        val mobFromOldSpawner = instance.nms.findMobFromOldSpawner(itemStack)

        if (mobFromOldSpawner != null) {
            Bukkit.getLogger().info { "Start to migrate OLD spawner for player ${event.player.uniqueId} to NEW spawner with type ${mobFromOldSpawner.uppercase()}" }
            val spawnerItemStack =
                itemInitService.initSpawnerItemStack(EntityType.valueOf(mobFromOldSpawner.uppercase()))
            event.player.inventory.remove(itemStack)
            event.player.inventory.addItem(spawnerItemStack)
            Bukkit.getLogger().info { "Finish to migrate OLD spawner for player ${event.player.uniqueId} to NEW spawner with type ${mobFromOldSpawner.uppercase()}" }
        }
    }

    private fun cancelUpdateSpawnerWithEggs(event: PlayerInteractEvent) {
        if (instance.defaultConfig.allowEggSpawnerChange) {
            return
        }

        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.clickedBlock?.type != Material.SPAWNER) {
            return
        }

        if (event.material.name.contains("SPAWN_EGG")) {
            event.isCancelled = true
            event.player.sendMessage(instance.translationConfig.errors.cancelUpdateSpawnerWithEggs.toColorHex())
        }
    }
}