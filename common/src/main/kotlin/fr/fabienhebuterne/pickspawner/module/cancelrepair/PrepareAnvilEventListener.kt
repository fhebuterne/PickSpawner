package fr.fabienhebuterne.pickspawner.module.cancelrepair

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack

class PrepareAnvilEventListener(
    private val instance: PickSpawner,
) : BaseListener<PrepareAnvilEvent>() {

    override fun execute(event: PrepareAnvilEvent) {
        cancelAnvilRepairOnCustomPickaxe(event)
    }

    private fun cancelAnvilRepairOnCustomPickaxe(event: PrepareAnvilEvent) {
        if (!instance.defaultConfig.cancelAnvilRepairCustomPickaxe) {
            return
        }

        val itemStack = event.inventory.getItem(0) ?: return
        val player = event.view.player as Player
        if (instance.defaultConfig.isCustomPickaxe(itemStack)) {
            event.result = ItemStack(Material.AIR)
            player.sendMessage(instance.translationConfig.errors.cancelAnvilRepairCustomPickaxe)
        }
    }


}
