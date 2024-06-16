package fr.fabienhebuterne.pickspawner.module.placespawner

import fr.fabienhebuterne.pickspawner.module.CommonListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteraction.entityTypeByPlayer
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceEventListener : Listener {

    @EventHandler
    fun execute(event: BlockPlaceEvent) {
        CommonListener.execute { blockPlaceEvent(event) }
    }

    private fun blockPlaceEvent(event: BlockPlaceEvent) {
        if (event.blockPlaced.type != Material.SPAWNER || !entityTypeByPlayer.containsKey(event.player.uniqueId)) {
            return
        }

        val blockState = event.blockPlaced.state
        val spawner = blockState as CreatureSpawner
        val entityType = entityTypeByPlayer[event.player.uniqueId]
        if (entityType != null) {
            spawner.spawnedType = entityType
            blockState.update()
        }
    }

}