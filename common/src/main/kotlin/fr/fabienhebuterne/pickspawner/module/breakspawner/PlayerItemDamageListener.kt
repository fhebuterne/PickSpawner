package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.inventory.meta.Damageable

class PlayerItemDamageListener(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : BaseListener<PlayerItemDamageEvent>() {

    override fun execute(event: PlayerItemDamageEvent) {
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
