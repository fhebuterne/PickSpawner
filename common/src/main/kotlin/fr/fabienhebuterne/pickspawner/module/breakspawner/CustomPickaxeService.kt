package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack

class CustomPickaxeService(
    private val instance: PickSpawner,
    private val spawnerItemStackService: SpawnerItemStackService
) {

    fun breakSpawnerWithCustomPickaxe(event: BlockBreakEvent, creatureSpawner: CreatureSpawner) {
        if (!instance.defaultConfig.allowPickaxeCustom) {
            return
        }

        val player = event.player
        val mainHandItem: ItemStack = player.inventory.itemInMainHand

        if (!instance.defaultConfig.isCustomPickaxe(mainHandItem, instance)) {
            event.isCancelled = true
            player.sendMessage(instance.translationConfig.errors.itemCannotBreakSpawner.toColorHex())
            return
        }

        spawnerItemStackService.breakSpawner(player, event.block.world, event.block.location, creatureSpawner)
    }

    fun cancelEventWhenPickaxeIsUsedOnNonSpawner(event: BlockBreakEvent) {
        val mainHandItem: ItemStack = event.player.inventory.itemInMainHand

        if (instance.defaultConfig.isCustomPickaxe(mainHandItem, instance)) {
            event.isCancelled = true
            event.player.sendMessage(instance.translationConfig.errors.pickaxeBreakOnlySpawner.toColorHex())
            return
        }
    }

}
