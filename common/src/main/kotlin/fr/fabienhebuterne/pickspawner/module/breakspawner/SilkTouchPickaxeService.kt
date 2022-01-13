package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.block.BlockBreakEvent

class SilkTouchPickaxeService(
    private val instance: PickSpawner,
    private val spawnerItemStackService: SpawnerItemStackService
) {

    fun breakSpawnerWithSilkTouchPickaxe(
        event: BlockBreakEvent,
        creatureSpawner: CreatureSpawner
    ): Boolean {
        if (!instance.defaultConfig.allowPickaxeSilkTouch) {
            return false
        }

        if (!event.player.inventory.itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            return false
        }

        spawnerItemStackService.breakSpawner(
            event.player,
            event.block.world,
            event.block.location,
            creatureSpawner
        )

        return true
    }

}
