package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.module.BaseListener
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakEventListener(
    private val instance: PickSpawner,
    private val silkTouchPickaxeService: SilkTouchPickaxeService,
    private val customPickaxeService: CustomPickaxeService,
    private val spawnerItemStackService: SpawnerItemStackService
) : BaseListener<BlockBreakEvent>() {

    override fun execute(event: BlockBreakEvent) {
        if (event.isCancelled) {
            return
        }

        val blockType = event.block.type
        val blockState = event.block.state

        if (blockType == Material.SPAWNER && blockState is CreatureSpawner) {
            keepCreatureOnSpawnerBreak(blockState)

            if(event.player.gameMode == GameMode.CREATIVE && event.player.isSneaking && event.player.hasPermission("pickspawner.creabreak"))
            {
                spawnerItemStackService.breakSpawner(event.player, event.block.world, event.block.location, blockState)
                return
            }

            val isBreakFromSilkTouch = silkTouchPickaxeService.breakSpawnerWithSilkTouchPickaxe(event, blockState)
            if (!isBreakFromSilkTouch) {
                customPickaxeService.breakSpawnerWithCustomPickaxe(event, blockState)
            }
        } else {
            // should cancel event when material is not a spawner with custom pickaxe
            customPickaxeService.cancelEventWhenPickaxeIsUsedOnNonSpawner(event)
        }
    }

    private fun keepCreatureOnSpawnerBreak(creatureSpawner: CreatureSpawner) {
        if (!instance.defaultConfig.allowKeepCreatureOnSpawnerBreak) {
            creatureSpawner.spawnedType = EntityType.PIG
        }
    }

}
