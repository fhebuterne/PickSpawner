package fr.fabienhebuterne.pickspawner.module.interactspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.ItemMeta

class PlayerInteractEventListener(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : BaseListener<PlayerInteractEvent>() {
    override fun execute(event: PlayerInteractEvent) {
        migrateOldSpawnerToNewSpawner(event)
        cancelUpdateSpawnerWithEggs(event)
    }

    companion object {
        var entityType : EntityType? = null;
    }

    private fun migrateOldSpawnerToNewSpawner(event: PlayerInteractEvent) {
        var itemStack = event.item

        if (itemStack == null || itemStack.type != Material.SPAWNER) {
            return
        }

        val mobFromOldSpawner = instance.nms.findMobFromOldSpawner(itemStack)

        if (mobFromOldSpawner != null) {
            Bukkit.getLogger().info { "Start to migrate OLD spawner for player ${event.player.uniqueId} to NEW spawner with type ${mobFromOldSpawner.uppercase()}" }
            val spawnerItemStack = itemInitService.initSpawnerItemStack(EntityType.valueOf(mobFromOldSpawner.uppercase()))
            if(event.hand == EquipmentSlot.HAND)
            {
                event.player.inventory.setItemInMainHand(spawnerItemStack)
            }
            else
            {
                event.player.inventory.setItemInOffHand(spawnerItemStack)
            }

            itemStack = spawnerItemStack

            Bukkit.getLogger().info { "Finish to migrate OLD spawner for player ${event.player.uniqueId} to NEW spawner with type ${mobFromOldSpawner.uppercase()}" }
        }

        entityType = ((itemStack.itemMeta as BlockStateMeta).blockState as CreatureSpawner).spawnedType;
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