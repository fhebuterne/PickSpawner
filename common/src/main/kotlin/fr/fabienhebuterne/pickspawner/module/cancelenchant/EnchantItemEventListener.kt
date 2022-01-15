package fr.fabienhebuterne.pickspawner.module.cancelenchant

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.event.enchantment.EnchantItemEvent

class EnchantItemEventListener(private val instance: PickSpawner): BaseListener<EnchantItemEvent>() {
    override fun execute(event: EnchantItemEvent) {
        cancelAddEnchantmentOnCustomPickaxe(event)
    }

    private fun cancelAddEnchantmentOnCustomPickaxe(event: EnchantItemEvent) {
        val player = event.enchanter

        if (instance.defaultConfig.isCustomPickaxe(event.item, instance)) {
            event.isCancelled = true
            player.sendMessage(instance.translationConfig.errors.cancelAddEnchantmentCustomPickaxe.toColorHex())
        }
    }
}