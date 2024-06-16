package fr.fabienhebuterne.pickspawner.nms

import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.EnumItemRarity
import net.minecraft.world.item.Item
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import java.lang.reflect.Field

class Utils_1_20_R4 : Utils {

    override fun setMaxStackSize(maxStack: Int) {
        try {
            /**
             * DataComponents.c is maxStackSize, other var is default value for blocks, see [DataComponents.af]
              */
            val dataComponentsUpdated: DataComponentMap = DataComponentMap.a().a(DataComponents.c, 1)
                .a(DataComponents.i, ItemLore.a)
                .a(DataComponents.k, ItemEnchantments.a)
                .a(DataComponents.r, 0)
                .a(DataComponents.n, ItemAttributeModifiers.a)
                .a(DataComponents.j, EnumItemRarity.a)
                .a()
            val dataComponentMapField: Field = Item::class.java.getDeclaredField("c")
            dataComponentMapField.isAccessible = true
            dataComponentMapField.set(Blocks.ct.r(), dataComponentsUpdated)
        } catch (e: Exception) {
            Bukkit.getLogger().warning("Can't update maxStackSize for spawner because : $e")
            e.printStackTrace()
        }
    }

}