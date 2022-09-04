package fr.fabienhebuterne.pickspawner.command

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import fr.fabienhebuterne.pickspawner.PickSpawner
import fr.fabienhebuterne.pickspawner.command.factory.AbstractCommand
import fr.fabienhebuterne.pickspawner.command.factory.CommandInfo
import fr.fabienhebuterne.pickspawner.config.EconomyType
import fr.fabienhebuterne.pickspawner.config.Price
import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import fr.fabienhebuterne.pickspawner.exception.BadArgumentException
import fr.fabienhebuterne.pickspawner.module.ItemInitService
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import java.util.stream.IntStream

const val BUY_COMMAND_USAGE =
    "/pickspawner buy <custom_pickaxe|custom_pickaxe_durability> <quantity|durability>"

@CommandInfo("buy", "pickspawner.buy", 1, BUY_COMMAND_USAGE)
class BuyCommand(
    private val instance: PickSpawner,
    private val itemInitService: ItemInitService
) : AbstractCommand() {

    override var literalArgumentBuilder: LiteralArgumentBuilder<String> =
        LiteralArgumentBuilder.literal<String?>("buy")
            .then(
                LiteralArgumentBuilder.literal<String?>("custom_pickaxe")
                    .then(LiteralArgumentBuilder.literal("quantity"))
                    .build()
            )
            .then(
                LiteralArgumentBuilder.literal<String?>("custom_pickaxe_durability")
                    .then(LiteralArgumentBuilder.literal("durability"))
                    .build()
            )

    override fun runOnPlayer(playerSender: Player, command: Command, label: String, args: Array<out String>) {
        buy(args, playerSender)
    }

    private fun buy(args: Array<out String>, playerSender: Player) {
        val quantityOrDurability: Int = args.getOrNull(1)?.toIntOrNull() ?: 1

        when (args[0].uppercase()) {
            "CUSTOM_PICKAXE" -> buyCustomPickaxe(playerSender, quantityOrDurability)
            "CUSTOM_PICKAXE_DURABILITY" -> buyCustomPickaxeDurability(playerSender, quantityOrDurability)
            else -> throw BadArgumentException(playerSender, GIVE_COMMAND_USAGE)
        }
    }

    private fun buyCustomPickaxe(playerSender: Player, quantity: Int) {
        val count = playerSender.inventory.storageContents.count { it == null || it.type == Material.AIR }
        if (count < quantity) {
            playerSender.sendMessage(instance.translationConfig.errors.missingPlaceInventoryBuyCancelled.toColorHex())
            return
        }

        val priceCustomPickaxe = instance.defaultConfig.priceCustomPickaxe
        val defaultDamage = instance.defaultConfig.defaultDamageOnCustomPickaxe
        if (priceCustomPickaxe.economyType == EconomyType.ITEM) {
            buyCustomPickaxeOnItemEconomy(priceCustomPickaxe, quantity, playerSender, defaultDamage)
        } else if (priceCustomPickaxe.economyType == EconomyType.MONEY) {
            buyCustomPickaxeOnMoneyEconomy(priceCustomPickaxe, quantity, playerSender, defaultDamage)
        }
    }

    private fun buyCustomPickaxeOnItemEconomy(
        priceCustomPickaxe: Price,
        quantity: Int,
        playerSender: Player,
        defaultDamage: Int
    ) {
        val quantityToBuy = priceCustomPickaxe.quantity * quantity

        val materialName = priceCustomPickaxe.materialName
            ?: throw IllegalStateException("missing material name in configuration to buy item")

        if (!playerSender.inventory.contains(materialName, quantityToBuy)) {
            playerSender.sendMessage(instance.translationConfig.errors.missingItemToBuy.toColorHex())
            return
        }

        playerSender.inventory.removeItem(ItemStack(materialName, quantityToBuy))
        IntStream.range(0, quantity).forEach {
            playerSender.inventory.addItem(itemInitService.initCustomPickaxeItemStack(defaultDamage))
        }
        playerSender.sendMessage(instance.translationConfig.buyCustomPickaxe.toColorHex())
    }

    private fun buyCustomPickaxeOnMoneyEconomy(
        priceCustomPickaxe: Price,
        quantity: Int,
        playerSender: Player,
        defaultDamage: Int
    ) {
        val price = priceCustomPickaxe.price ?: throw IllegalStateException("missing price in configuration")
        val priceToBuy = price * quantity

        if (!instance.getEconomy().has(playerSender, priceToBuy.toDouble())) {
            playerSender.sendMessage(instance.translationConfig.errors.missingMoneyToBuy.toColorHex())
            return
        }

        val withdrawPlayer = instance.getEconomy().withdrawPlayer(playerSender, priceToBuy.toDouble())
        if (withdrawPlayer.transactionSuccess()) {
            IntStream.range(0, quantity).forEach {
                playerSender.inventory.addItem(itemInitService.initCustomPickaxeItemStack(defaultDamage))
            }
            playerSender.sendMessage(instance.translationConfig.buyCustomPickaxe.toColorHex())
        }
    }

    private fun buyCustomPickaxeDurability(playerSender: Player, durability: Int) {
        val itemInMainHand = playerSender.inventory.itemInMainHand

        if (!instance.defaultConfig.isCustomPickaxe(itemInMainHand, instance)) {
            playerSender.sendMessage(instance.translationConfig.errors.cancelBuyDurabilityMissingCustomPickaxeInMainHand.toColorHex())
            return
        }

        val itemMeta = itemInMainHand.itemMeta
        val maxDurability = itemInMainHand.type.maxDurability
        val damage = (itemMeta as Damageable).damage
        val totalDurability = (maxDurability - damage) + durability

        if (totalDurability > maxDurability) {
            playerSender.sendMessage(instance.translationConfig.errors.cancelBuyDurabilityExceedMaxDurability.toColorHex())
            return
        }

        val priceAddingDurabilityOnCustomPickaxe = instance.defaultConfig.priceAddingDurabilityOnCustomPickaxe
        if (priceAddingDurabilityOnCustomPickaxe.economyType == EconomyType.ITEM) {
            buyCustomPickaxeDurabilityOnItemEconomy(
                priceAddingDurabilityOnCustomPickaxe,
                durability,
                playerSender,
                itemMeta,
                damage,
                itemInMainHand,
                maxDurability
            )
        } else if (priceAddingDurabilityOnCustomPickaxe.economyType == EconomyType.MONEY) {
            buyCustomPickaxeDurabilityOnMoneyEconomy(
                priceAddingDurabilityOnCustomPickaxe,
                durability,
                playerSender,
                itemMeta,
                damage,
                itemInMainHand,
                maxDurability
            )
        }
    }

    private fun buyCustomPickaxeDurabilityOnItemEconomy(
        priceAddingDurabilityOnCustomPickaxe: Price,
        durability: Int,
        playerSender: Player,
        itemMeta: Damageable,
        damage: Int,
        itemInMainHand: ItemStack,
        maxDurability: Short
    ) {
        val quantityToBuy = priceAddingDurabilityOnCustomPickaxe.quantity * durability

        val materialName = priceAddingDurabilityOnCustomPickaxe.materialName
            ?: throw IllegalStateException("missing material name in configuration to buy item")

        if (!playerSender.inventory.contains(materialName, quantityToBuy)) {
            playerSender.sendMessage(instance.translationConfig.errors.missingItemToBuy.toColorHex())
            return
        }

        playerSender.inventory.removeItem(ItemStack(materialName, quantityToBuy))
        itemMeta.damage = damage - durability
        itemInMainHand.itemMeta = itemInitService.refreshDurabilityOnItemMetaLore(itemMeta, maxDurability.toInt())
        playerSender.sendMessage(instance.translationConfig.buyCustomPickaxeDurability.toColorHex())
    }

    private fun buyCustomPickaxeDurabilityOnMoneyEconomy(
        priceAddingDurabilityOnCustomPickaxe: Price,
        durability: Int,
        playerSender: Player,
        itemMeta: Damageable,
        damage: Int,
        itemInMainHand: ItemStack,
        maxDurability: Short
    ) {
        val price =
            priceAddingDurabilityOnCustomPickaxe.price ?: throw IllegalStateException("missing price in configuration")
        val priceToBuy = price * durability

        if (!instance.getEconomy().has(playerSender, priceToBuy.toDouble())) {
            playerSender.sendMessage(instance.translationConfig.errors.missingMoneyToBuy.toColorHex())
            return
        }

        val withdrawPlayer = instance.getEconomy().withdrawPlayer(playerSender, priceToBuy.toDouble())
        if (withdrawPlayer.transactionSuccess()) {
            itemMeta.damage = damage - durability
            itemInMainHand.itemMeta = itemInitService.refreshDurabilityOnItemMetaLore(itemMeta, maxDurability.toInt())
            playerSender.sendMessage(instance.translationConfig.buyCustomPickaxeDurability.toColorHex())
        }
    }

}