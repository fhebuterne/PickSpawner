package fr.fabienhebuterne.pickspawner.config

import fr.fabienhebuterne.pickspawner.PickSpawner
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class MigrationPickaxeConfig(
    val enabled: Boolean = false,
    val pickaxeName: String? = null,
    val pickaxeLore: List<String> = listOf(),
    val loreToExclude: String? = null,
    val loreToDetect: String? = null
) : ConfigType {
    fun isOlderCustomPickaxe(itemStack: ItemStack, instance: PickSpawner): Boolean {
        return itemStack.itemMeta?.hasDisplayName() == true
                && itemStack.itemMeta?.displayName == pickaxeName
                && itemStack.itemMeta?.hasLore() == true
                && itemStack.itemMeta?.lore?.contains(loreToDetect) == true
                && itemStack.itemMeta?.persistentDataContainer?.has(
            NamespacedKey(instance, "CustomPickaxe"),
            PersistentDataType.STRING
        ) == false
    }
}
