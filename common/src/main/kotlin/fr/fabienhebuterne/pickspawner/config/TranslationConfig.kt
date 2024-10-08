package fr.fabienhebuterne.pickspawner.config

import fr.fabienhebuterne.pickspawner.config.TranslationConfig.Companion.toColorHex
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.UUID
import java.util.regex.Matcher
import java.util.regex.Pattern

data class TranslationConfig(
    val entity: Map<EntityType, String> = mapOf(),
    val spawnerDisplayName: String,
    val spawnerLore: List<String> = listOf(),
    val spawnerPlayerBreak: String,
    val errors: ErrorConfig,
    val reloadInProgress: String,
    val reloadEnded: String,
    val pickaxeHasBeenMigrated: String,
    val buyCustomPickaxe: String,
    val buyCustomPickaxeDurability: String,
) : ConfigType {

    fun getEntityOrDefault(entityType: EntityType): String {
        return entity.getOrDefault(entityType, entityType.name)
    }

    fun getSpawnerDisplayName(entityType: EntityType): String {
        return spawnerDisplayName.replace(
            "{{entity}}",
            getEntityOrDefault(entityType)
        ).toColorHex()
    }

    fun getSpawnerPlayerBreak(
        playerPseudo: String,
        entityType: EntityType,
        location: Location
    ): String {
        return spawnerPlayerBreak
            .replace("{{playerPseudo}}", playerPseudo)
            .replace("{{entity}}", getEntityOrDefault(entityType))
            .replace("{{world}}", location.world?.name ?: "")
            .replace("{{x}}", location.x.toString())
            .replace("{{y}}", location.y.toString())
            .replace("{{z}}", location.z.toString())
            .toColorHex()
    }

    companion object {
        // Work with new hex color code and old color code
        fun String.toColorHex(): String {
            var message = this
            val pattern: Pattern = Pattern.compile("\\{#[a-fA-F0-9]{6}}")
            var matcher: Matcher = pattern.matcher(message)
            while (matcher.find()) {
                val hexCode = message.substring(matcher.start(), matcher.end())
                var replaceSharp = hexCode.replace('#', 'x')
                replaceSharp = replaceSharp.replace("{", "")
                replaceSharp = replaceSharp.replace("}", "")
                val ch = replaceSharp.toCharArray()
                val builder = StringBuilder("")
                for (c in ch) {
                    builder.append("&$c")
                }
                message = message.replace(hexCode, builder.toString())
                matcher = pattern.matcher(message)
            }
            return ChatColor.translateAlternateColorCodes('&', message)
        }
    }
}

data class ErrorConfig(
    val missingPermission: String,
    val cancelAnvilRepairCustomPickaxe: String,
    val cancelAddEnchantmentCustomPickaxe: String,
    val cancelUpdateSpawnerWithEggs: String,
    val cancelDamageEntityCustomPickaxe: String,
    val cancelRepairFromCommand: String,
    val itemCannotBreakSpawner: String,
    val pickaxeBreakOnlySpawner: String,
    val missingPlaceInventoryBuyCancelled: String,
    val missingPlaceInventoryGiveDrop: String,
    val cancelBuyDurabilityBadDurability: String,
    val missingMoneyToBuy: String,
    val missingItemToBuy: String,
    val cancelBuyDurabilityMissingCustomPickaxeInMainHand: String,
    val cancelBuyDurabilityExceedMaxDurability: String,
    val missingSpawnedTypeOnPlayer: String = "&8[&e&lPickSpawner&8] &cCan't place this spawner because missing spawnerType, contact an staff member",
    val missingSpawnedTypeOnLogs: String = "&8[&e&lPickSpawner&8] &cPlayer with name {{playerPseudo}} and uuid {{playerUUID}} can't place spawner because missing spawnerType",
) {

    fun getMissingSpawnedTypeOnLogs(
        playerPseudo: String,
        playerUUID: UUID,
    ): String {
        return missingSpawnedTypeOnLogs
            .replace("{{playerPseudo}}", playerPseudo)
            .replace("{{playerUUID}}", playerUUID.toString())
            .toColorHex()
    }

}