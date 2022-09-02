package fr.fabienhebuterne.pickspawner.nms

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

class Utils_1_19_R1 : Utils {

    override fun findMobFromOldSpawner(itemStack: ItemStack): String? {
        val asNMSCopy = CraftItemStack.asNMSCopy(itemStack)
        val nbtTagCompound = asNMSCopy.u()
        if (nbtTagCompound != null && nbtTagCompound.e("PickSpawner")) {
            val pickSpawnerCompound = nbtTagCompound.c("PickSpawner") as NBTTagCompound
            return (pickSpawnerCompound.c("id") as NBTTagString).e_()
        }
        return null
    }
    override fun setMaxStackSize(maxStack: Int) {
        try {
            val maxStackSizeField: Field = Item::class.java.getDeclaredField("d")
            maxStackSizeField.isAccessible = true
            maxStackSizeField.setInt(Blocks.bV.l(), maxStack)
        } catch (e: Exception) {
            Bukkit.getLogger()
                .warning("Can't update maxStackSize for spawner because : $e")
            e.printStackTrace()
        }
    }
}