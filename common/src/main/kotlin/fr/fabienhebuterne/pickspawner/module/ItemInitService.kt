package fr.fabienhebuterne.pickspawner.module

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta
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

    fun initCustomPickaxeItemStack(): ItemStack {
        val customMaterial = instance.defaultConfig.materialCustomPickaxe
            ?: throw IllegalStateException("Missing custom pickaxe material in configuration")

        val itemStack = ItemStack(customMaterial, 1)
        val itemMeta = itemStack.itemMeta
        itemMeta?.setDisplayName(instance.defaultConfig.nameCustomPickaxe?.toColorHex())
        itemMeta?.lore = instance.defaultConfig.loreCustomPickaxe.map { it.toColorHex() }
        itemMeta?.persistentDataContainer?.set(NamespacedKey(instance, "CustomPickaxe"), PersistentDataType.STRING, "true")
        itemStack.itemMeta = itemMeta
        return itemStack
    }

}