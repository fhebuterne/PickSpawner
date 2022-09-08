package fr.fabienhebuterne.pickspawner.module.pickaxeMigration

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.Damageable
import org.bukkit.persistence.PersistentDataType

class PickaxeMigrationPlayerInteractEventListener(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) :
    BaseListener<PlayerInteractEvent>() {
    override fun execute(event: PlayerInteractEvent) {
        migrationFromOldToNewPickaxe(event)
    }

    private fun migrationFromOldToNewPickaxe(event: PlayerInteractEvent) {
        if (!instance.migrationPickaxeConfig.enabled) {
            return
        }

        val item = event.item

        if (item?.hasItemMeta() == false || item?.itemMeta?.persistentDataContainer?.has(
                NamespacedKey(
                    instance,
                    "CustomPickaxe"
                ), PersistentDataType.STRING
            ) == true
        ) {
            return
        }


        val itemMeta = item?.itemMeta
        val pickaxeNameIsPresent =
            instance.migrationPickaxeConfig.pickaxeName != null && itemMeta?.hasDisplayName() == true
        val loreIsPresent = instance.migrationPickaxeConfig.pickaxeLore.isNotEmpty() && itemMeta?.hasLore() == true

        if (pickaxeNameIsPresent || loreIsPresent) {
            val loreToExclude = instance.migrationPickaxeConfig.loreToExclude

            var itemLore = if (loreToExclude != null) {
                itemMeta?.lore?.filterNot { it.contains(loreToExclude) }
            } else {
                itemMeta?.lore
            }

            itemLore = itemLore?.map { s -> s.replace("§4?","§4➤") }

            if (instance.migrationPickaxeConfig.pickaxeName == itemMeta?.displayName && instance.migrationPickaxeConfig.pickaxeLore == itemLore) {
                Bukkit.getLogger().info { "[PickSpawner] Migration OLD pickaxe for player ${event.player.uniqueId} to NEW pickaxe" }
                val damage = (itemMeta as Damageable).damage
                if(event.hand == EquipmentSlot.HAND)
                {
                    event.player.inventory.setItemInMainHand(itemInitService.initCustomPickaxeItemStack(damage))
                }
                else
                {
                    event.player.inventory.setItemInOffHand(itemInitService.initCustomPickaxeItemStack(damage))
                }

                event.player.sendMessage(instance.translationConfig.pickaxeHasBeenMigrated.toColorHex())
            }
        }

    }
}