package fr.fabienhebuterne.pickspawner.module.placespawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.CommonListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteraction.entityTypeByPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceEventListener(
    private val instance: PickSpawner
) : Listener {

    @EventHandler
    fun execute(event: BlockPlaceEvent) {
        CommonListener.execute { blockPlaceEvent(event) }
    }

    private fun blockPlaceEvent(event: BlockPlaceEvent) {
        if (event.blockPlaced.type != Material.SPAWNER) {
            return
        }

        val blockState = event.blockPlaced.state
        val spawner = blockState as CreatureSpawner
        val entityType = entityTypeByPlayer[event.player.uniqueId]
        if (entityType != null) {
            spawner.spawnedType = entityType
            blockState.update()
            entityTypeByPlayer.remove(event.player.uniqueId)
        } else {
            val player = event.player
            Bukkit.getLogger().warning(instance.translationConfig.errors.getMissingSpawnedTypeOnLogs(player.name, player.uniqueId))
            player.sendMessage(instance.translationConfig.errors.missingSpawnedTypeOnPlayer.toColorHex())
            event.isCancelled = true
        }
    }

}