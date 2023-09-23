package fr.fabienhebuterne.pickspawner.module.interactspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.CommonListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.BlockStateMeta

class PlayerInteractEventListener(
    private val instance: PickSpawner
) : Listener {

    @EventHandler
    fun execute(event: PlayerInteractEvent) {
        CommonListener.execute { playerInteractEvent(event) }
    }

    private fun playerInteractEvent(event: PlayerInteractEvent) {
        migrateOldSpawnerToNewSpawner(event)
        cancelUpdateSpawnerWithEggs(event)
    }

    companion object {
        var entityType: EntityType? = null;
    }

    private fun migrateOldSpawnerToNewSpawner(event: PlayerInteractEvent) {
        var itemStack = event.item

        if (itemStack == null || itemStack.type != Material.SPAWNER) {
            return
        }

        val mobFromOldSpawner = instance.nms.findMobFromOldSpawner(itemStack)

        if (mobFromOldSpawner != null) {
            entityType = EntityType.valueOf(mobFromOldSpawner.uppercase())
        } else {
            entityType = ((itemStack.itemMeta as BlockStateMeta).blockState as CreatureSpawner).spawnedType
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