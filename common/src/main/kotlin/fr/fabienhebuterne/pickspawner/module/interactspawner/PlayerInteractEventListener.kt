package fr.fabienhebuterne.pickspawner.module.interactspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PlayerInteractEventListener(private val instance: PickSpawner): BaseListener<PlayerInteractEvent>() {
    override fun execute(event: PlayerInteractEvent) {
        cancelUpdateSpawnerWithEggs(event)
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