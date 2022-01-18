package fr.fabienhebuterne.pickspawner.module.entitydamage

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class EntityDamageByEntityEventListener(private val instance: PickSpawner) : BaseListener<EntityDamageEvent>() {
    override fun execute(event: EntityDamageEvent) {
        // This is a workaround because event from EntityDamageEvent are passed when EntityDamageByEntityEvent is registered strange...
        if (event !is EntityDamageByEntityEvent) {
            return
        }

        cancelUsingCustomPickaxeOnEntities(event)
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