package fr.fabienhebuterne.pickspawner.config

import fr.fabienhebuterne.pickspawner.PickSpawner
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class DefaultConfig(
    val debug: Boolean = false,
    val allowEggSpawnerChange: Boolean = false,
    val spawnersStackable: Boolean = false,
    val allowPickaxeSilkTouch: Boolean = true,
    val allowPickaxeCustom: Boolean = false,
    val allowKeepCreatureOnSpawnerBreak: Boolean = true,
    val priceCustomPickaxe: Price = Price(),
    val priceAddingDurabilityOnCustomPickaxe: Price = Price(),
    val nameCustomPickaxe: String? = null,
    val materialCustomPickaxe: Material? = Material.IRON_PICKAXE,
    val loreCustomPickaxe: List<String> = listOf(),
    val cancelAnvilRepairCustomPickaxe: Boolean = false,
    val detectLoreInPickaxeToRestrictRepair: String? = null
): ConfigType {
    fun isCustomPickaxe(itemStack: ItemStack, instance: PickSpawner): Boolean {
        return itemStack.itemMeta?.persistentDataContainer?.has(NamespacedKey(instance, "CustomPickaxe"), PersistentDataType.STRING) == true
    }
}

enum class EconomyType {
    ITEM,
    MONEY
}

data class Price(
    val economyType: EconomyType = EconomyType.MONEY,
    val price: Long? = 100000,
    val quantity: Int? = 1,
    val materialName: Material? = null
)
