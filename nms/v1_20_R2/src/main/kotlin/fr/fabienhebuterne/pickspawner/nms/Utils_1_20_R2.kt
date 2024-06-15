package fr.fabienhebuterne.pickspawner.nms

import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import java.lang.reflect.Field

class Utils_1_20_R2 : Utils {

    override fun setMaxStackSize(maxStack: Int) {
        try {
            val maxStackSizeField: Field = Item::class.java.getDeclaredField("d")
            maxStackSizeField.isAccessible = true
            maxStackSizeField.setInt(Blocks.ct.k(), maxStack)
        } catch (e: Exception) {
            Bukkit.getLogger()
                .warning("Can't update maxStackSize for spawner because : $e")
            e.printStackTrace()
        }
    }

}