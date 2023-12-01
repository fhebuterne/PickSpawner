package fr.fabienhebuterne.pickspawner.nms

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagString
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Blocks
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.reflect.Field

class Utils_1_20_R2 : Utils {

    override fun findMobFromOldSpawner(itemStack: ItemStack): String? {
        val asNMSCopy = CraftItemStack.asNMSCopy(itemStack)
        val nbtTagCompound = asNMSCopy.v() ?: return null
        if (nbtTagCompound.e("PickSpawner")) {
            val pickSpawnerCompound = nbtTagCompound.c("PickSpawner") as NBTTagCompound
            return (pickSpawnerCompound.c("id") as NBTTagString).r_()
        } else if (nbtTagCompound.e("BlockEntityTag")) {
            val blockEntityTag = nbtTagCompound.c("BlockEntityTag") as NBTTagCompound

            if (blockEntityTag.e("EntityId")) {
                return fixName((blockEntityTag.c("EntityId") as NBTTagString).r_())
            } else if (blockEntityTag.e("SpawnData")) {
                val spawnData = blockEntityTag.c("SpawnData") as NBTTagCompound;
                if (spawnData.e("id")) {
                    return fixName((spawnData.c("id") as NBTTagString).r_())
                }
            }
        }

        return null
    }

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