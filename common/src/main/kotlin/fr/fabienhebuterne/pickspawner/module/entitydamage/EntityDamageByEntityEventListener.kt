package fr.fabienhebuterne.pickspawner.module.entitydamage

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.CommonListener
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageByEntityEventListener(private val instance: PickSpawner) : Listener {

    @EventHandler
    fun execute(event: EntityDamageByEntityEvent) {
        CommonListener.execute { cancelUsingCustomPickaxeOnEntities(event) }
    }

    private fun cancelUsingCustomPickaxeOnEntities(event: EntityDamageByEntityEvent) {
        val damager = event.damager

        if (damager !is Player) {
            return
        }

        if (instance.defaultConfig.isCustomPickaxe(damager.inventory.itemInMainHand, instance)) {
            event.isCancelled = true
            damager.sendMessage(instance.translationConfig.errors.cancelDamageEntityCustomPickaxe.toColorHex())
        }
    }
    
}