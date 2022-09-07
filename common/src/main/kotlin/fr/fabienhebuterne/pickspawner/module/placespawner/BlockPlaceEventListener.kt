package fr.fabienhebuterne.pickspawner.module.placespawner

import fr.fabienhebuterne.pickspawner.module.BaseListener
import fr.fabienhebuterne.pickspawner.module.interactspawner.PlayerInteractEventListener
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.block.BlockPlaceEvent

class BlockPlaceEventListener : BaseListener<BlockPlaceEvent>()
{
    override fun execute(event: BlockPlaceEvent)
    {
        if (event.blockPlaced.type != Material.SPAWNER && PlayerInteractEventListener.entityType != null) {
            return
        }

        val blockState = event.blockPlaced.state;
        val spawner = blockState as CreatureSpawner;
        spawner.spawnedType = PlayerInteractEventListener.entityType!!;
        blockState.update();
    }
}