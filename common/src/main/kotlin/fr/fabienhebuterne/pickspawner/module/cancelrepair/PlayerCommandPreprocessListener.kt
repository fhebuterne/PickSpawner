package fr.fabienhebuterne.pickspawner.module.cancelrepair

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class PlayerCommandPreprocessListener(
    private val instance: PickSpawner,
) : BaseListener<PlayerCommandPreprocessEvent>() {

    override fun execute(event: PlayerCommandPreprocessEvent) {
        if (!event.message.startsWith("/cmi repair")) {
            return
        }

        if (!instance.defaultConfig.cancelRepairFromCmiCommand) {
            return
        }

        val inventory: PlayerInventory = event.player.inventory
        val mainHand = inventory.itemInMainHand
        val offHand = inventory.itemInOffHand

        checkHand(event, mainHand)
        checkHand(event, offHand)
    }

    private fun checkHand(e: PlayerCommandPreprocessEvent, hand: ItemStack) {
        if (instance.defaultConfig.isCustomPickaxe(hand, instance)
            || instance.migrationPickaxeConfig.isOlderCustomPickaxe(hand, instance)
        ) {
            e.player.sendMessage("§cVous ne pouvez pas réparer la pioche à spawner !")
            e.isCancelled = true
        }
    }

}