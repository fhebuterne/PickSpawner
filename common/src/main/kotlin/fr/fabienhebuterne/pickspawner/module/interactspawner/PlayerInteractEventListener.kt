package fr.fabienhebuterne.pickspawner.module.interactspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.CommonListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteraction.entityTypeByPlayer
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.BlockStateMeta
import java.util.*

class PlayerInteractEventListener(
    private val instance: PickSpawner
) : Listener {

    @EventHandler
    fun execute(event: PlayerInteractEvent) {
        CommonListener.execute { playerInteractEvent(event) }
    }

    private fun playerInteractEvent(event: PlayerInteractEvent) {
        setEntityTypeOnCurrentPlayerForBlockPlace(event)
        cancelUpdateSpawnerWithEggs(event)
    }

    private fun setEntityTypeOnCurrentPlayerForBlockPlace(event: PlayerInteractEvent) {
        val itemStack = event.item

        if (itemStack == null || itemStack.type != Material.SPAWNER) {
            return
        }

        val entityType = ((itemStack.itemMeta as BlockStateMeta).blockState as CreatureSpawner).spawnedType
        entityTypeByPlayer[event.player.uniqueId] = entityType
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