package fr.fabienhebuterne.pickspawner.module.breakspawner

import fr.fabienhebuterne.pickspawner.PickSpawner
import org.bukkit.block.CreatureSpawner
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class CustomPickaxeService(
    private val instance: PickSpawner,
    private val spawnerItemStackService: SpawnerItemStackService
) {

    fun breakSpawnerWithCustomPickaxe(event: BlockBreakEvent, creatureSpawner: CreatureSpawner) {
        if (!instance.defaultConfig.allowPickaxeCustom) {
            return
        }

        val player = event.player
        val mainHandItem: ItemStack = player.inventory.itemInMainHand

        if (!instance.defaultConfig.isCustomPickaxe(mainHandItem)) {
            event.isCancelled = true
            player.sendMessage("§8[§e§l!§8] §cCet item ne permet pas de casser les spawners !")
            return
        }

        val pickaxeMeta: ItemMeta? = mainHandItem.itemMeta
        val durability = mainHandItem.durability
        val loreResult = (249 - durability).toShort()
        val loreFinal: MutableList<String> = instance.defaultConfig.loreCustomPickaxe.toMutableList()
        pickaxeMeta?.lore = loreFinal
            .filter { it.contains("{{usage}}") }
            .map {
                it.replace(
                    "\\{\\{usage}}".toRegex(),
                    loreResult.toString()
                )
            }

        mainHandItem.itemMeta = pickaxeMeta
        spawnerItemStackService.breakSpawner(player, event.block.world, event.block.location, creatureSpawner)
    }

    fun cancelEventWhenPickaxeIsUsedOnNonSpawner(event: BlockBreakEvent) {
        val mainHandItem: ItemStack = event.player.inventory.itemInMainHand

        if (instance.defaultConfig.isCustomPickaxe(mainHandItem)) {
            event.isCancelled = true
            event.player.sendMessage("§8[§e§l!§8] §cCette pioche ne peut casser que les spawners !")
            return
        }
    }

}
