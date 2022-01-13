package fr.fabienhebuterne.pickspawner.config

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

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
    // TODO : Add check with custom NBT tag here
    fun isCustomPickaxe(itemStack: ItemStack): Boolean {
        return (itemStack.type == materialCustomPickaxe
                && loreCustomPickaxe == (itemStack.itemMeta?.lore ?: listOf<String>()))
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
