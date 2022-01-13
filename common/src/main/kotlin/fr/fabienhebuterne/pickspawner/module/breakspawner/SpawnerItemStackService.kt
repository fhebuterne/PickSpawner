package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

class SpawnerItemStackService(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) {

    fun breakSpawner(
        player: Player,
        world: World,
        location: Location,
        creatureSpawner: CreatureSpawner
    ) {
        if (instance.defaultConfig.debug) {
            Bukkit.getLogger().info(
                instance.translationConfig.getSpawnerPlayerBreak(
                    player.name,
                    creatureSpawner.spawnedType,
                    location
                )
            )
        }

        val itemStack = itemInitService.initSpawnerItemStack(creatureSpawner.spawnedType)

        if (player.inventory.firstEmpty() != -1) {
            player.inventory.addItem(itemStack)
        } else {
            world.dropItemNaturally(location, itemStack)
        }
    }

}
