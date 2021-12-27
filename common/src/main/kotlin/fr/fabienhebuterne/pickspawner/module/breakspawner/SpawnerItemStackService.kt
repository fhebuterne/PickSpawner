package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

class SpawnerItemStackService(private val instance: PickSpawner) {

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

        val itemStack = initSpawnerItemStack(creatureSpawner)

        if (player.inventory.firstEmpty() != -1) {
            player.inventory.addItem(itemStack)
        } else {
            world.dropItemNaturally(location, itemStack)
        }
    }

    private fun initSpawnerItemStack(creatureSpawner: CreatureSpawner): ItemStack {
        val itemStack = ItemStack(Material.SPAWNER, 1)
        var itemMeta = itemStack.itemMeta
        val blockStateMeta = itemMeta as BlockStateMeta
        val blockState = blockStateMeta.blockState as CreatureSpawner
        blockState.spawnedType = creatureSpawner.spawnedType
        blockStateMeta.blockState = blockState
        itemMeta = blockStateMeta
        itemMeta.setDisplayName(instance.translationConfig.getSpawnerDisplayName(creatureSpawner.spawnedType))
        itemStack.itemMeta = itemMeta
        return itemStack
    }

}
