package fr.fabienhebuterne.pickspawner.module

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

class ItemInitService(private val instance: PickSpawner) {

    fun initSpawnerItemStack(entityType: EntityType): ItemStack {
        val itemStack = ItemStack(Material.SPAWNER, 1)
        var itemMeta = itemStack.itemMeta
        val blockStateMeta = itemMeta as BlockStateMeta
        val blockState = blockStateMeta.blockState as CreatureSpawner
        blockState.spawnedType = entityType
        blockStateMeta.blockState = blockState
        itemMeta = blockStateMeta
        itemMeta.setDisplayName(instance.translationConfig.getSpawnerDisplayName(entityType))
        itemMeta.lore = instance.translationConfig.spawnerLore.map { it.toColorHex() }
        itemStack.itemMeta = itemMeta
        return itemStack
    }

    fun initCustomPickaxeItemStack(damage: Int = 0): ItemStack {
        val customMaterial = instance.defaultConfig.materialCustomPickaxe
            ?: throw IllegalStateException("Missing custom pickaxe material in configuration")

        val itemStack = ItemStack(customMaterial, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta?.setDisplayName(instance.defaultConfig.nameCustomPickaxe?.toColorHex())
        itemMeta?.lore = instance.defaultConfig.loreCustomPickaxe.map { it.toColorHex() }
        itemMeta?.persistentDataContainer?.set(
            NamespacedKey(instance, "CustomPickaxe"),
            PersistentDataType.STRING,
            "true"
        )
        if (itemMeta is Damageable) {
            itemMeta.damage = damage
        }
        itemStack.itemMeta = itemMeta?.let { refreshDurabilityOnItemMetaLore(it, itemStack.type.maxDurability.toInt()) }
        return itemStack
    }

    fun refreshDurabilityOnItemMetaLore(
        itemMeta: ItemMeta,
        maxDurability: Int,
        currentDurability: Int? = null
    ): ItemMeta {
        val durability = currentDurability ?: if (itemMeta is Damageable) {
            itemMeta.damage
        } else {
            0
        }

        val loreResult = maxDurability - durability
        val loreFinal: MutableList<String> = instance.defaultConfig.loreCustomPickaxe.toMutableList()
        itemMeta.lore = loreFinal
            .map {
                it.replace(
                    "\\{\\{usage}}".toRegex(),
                    loreResult.toString()
                )
            }
            .map { it.toColorHex() }

        return itemMeta
    }

}