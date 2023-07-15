package fr.fabienhebuterne.pickspawner.nms

import org.bukkit.inventory.ItemStack
import java.util.*

interface Utils {

    fun findMobFromOldSpawner(itemStack: ItemStack): String?
    fun setMaxStackSize(maxStack: Int)
    fun fixName(entity: String): String? {
        return when (entity.lowercase(Locale.getDefault())) {
            "cavespider" -> "cave_spider"
            "lavaslime" -> "magma_cube"
            "villagergolem" -> "iron_golem"
            "mushroomcow" -> "mushroom_cow"
            "polarbear" -> "polar_bear"
            "pigzombie" -> "zombified_piglin"
            "ozelot" -> "ocelot"
            "entityhorse" -> "horse"
            else -> entity
        }
    }

}