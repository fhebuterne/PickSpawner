package fr.fabienhebuterne.pickspawner.module.pickaxeMigration

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class PickaxeMigrationPlayerInteractEventListener(private val instance: PickSpawner) :
    BaseListener<PlayerInteractEvent>() {
    override fun execute(event: PlayerInteractEvent) {
        migrationFromOldToNewPickaxe(event)
    }

    // TODO : CHECK if pickaxe is not NEW pickaxe by checking tag
    private fun migrationFromOldToNewPickaxe(event: PlayerInteractEvent) {
        if (!instance.migrationPickaxeConfig.enabled) {
            return
        }

        val item = event.item

        if (item?.hasItemMeta() == false) {
            return
        }


        val itemMeta = item?.itemMeta
        val pickaxeNameIsPresent =
            instance.migrationPickaxeConfig.pickaxeName != null && itemMeta?.hasDisplayName() == true
        val loreIsPresent = instance.migrationPickaxeConfig.pickaxeLore.isNotEmpty() && itemMeta?.hasLore() == true

        if (pickaxeNameIsPresent || loreIsPresent) {
            val loreToExclude = instance.migrationPickaxeConfig.loreToExclude

            val itemLore = if (loreToExclude != null) {
                itemMeta?.lore?.filterNot { it.contains(loreToExclude) }
            } else {
                itemMeta?.lore
            }

            if (instance.migrationPickaxeConfig.pickaxeName == itemMeta?.displayName && instance.migrationPickaxeConfig.pickaxeLore == itemLore) {
                // Should migrate this pickaxe
                Bukkit.getLogger().info { "Migration OLD pickaxe for player ${event.player.uniqueId} to NEW pickaxe" }
            }
        }

    }
}