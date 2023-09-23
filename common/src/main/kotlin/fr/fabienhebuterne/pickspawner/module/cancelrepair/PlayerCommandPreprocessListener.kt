package fr.fabienhebuterne.pickspawner.module.cancelrepair

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.CommonListener
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

class PlayerCommandPreprocessListener(
    private val instance: PickSpawner,
) : Listener {

    @EventHandler
    fun execute(event: PlayerCommandPreprocessEvent) {
        CommonListener.execute { playerCommandPreprocessEvent(event) }
    }

    private fun playerCommandPreprocessEvent(event: PlayerCommandPreprocessEvent) {
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
            e.player.sendMessage(instance.translationConfig.errors.cancelRepairFromCommand.toColorHex())
            e.isCancelled = true
        }
    }

}