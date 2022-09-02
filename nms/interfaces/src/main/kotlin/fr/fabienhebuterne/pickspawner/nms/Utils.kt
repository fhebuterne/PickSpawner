package fr.fabienhebuterne.pickspawner.nms

import org.bukkit.inventory.ItemStack

interface Utils {

    fun findMobFromOldSpawner(itemStack: ItemStack): String?
    fun setMaxStackSize(maxStack: Int)

}