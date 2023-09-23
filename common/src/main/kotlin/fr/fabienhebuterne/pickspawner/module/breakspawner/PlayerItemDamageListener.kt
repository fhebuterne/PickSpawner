package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.CommonListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.meta.Damageable

class PlayerItemDamageListener(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : Listener {

    @EventHandler
    fun execute(event: PlayerItemDamageEvent) {
        CommonListener.execute { playerItemDamageEvent(event) }
    }

    fun playerItemDamageEvent(event: PlayerItemDamageEvent) {
        if (event.isCancelled) {
            return
        }

        val item = event.item

        if (instance.defaultConfig.isCustomPickaxe(item, instance)) {
            item.itemMeta = item.itemMeta?.let {
                val durability = if (it is Damageable) {
                    it.damage + event.damage
                } else {
                    null
                }

                itemInitService.refreshDurabilityOnItemMetaLore(
                    it,
                    item.type.maxDurability.toInt(),
                    durability
                )
            }
        }
    }

}
