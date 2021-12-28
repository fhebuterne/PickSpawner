package fr.fabienhebuterne.pickspawner.config

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.EntityType
import java.util.regex.Matcher
import java.util.regex.Pattern

data class TranslationConfig(
    val entity: Map<EntityType, String> = mapOf(),
    val spawnerDisplayName: String,
    val spawnerPlayerBreak: String,
    val errors: ErrorConfig
) : ConfigType {
    fun getEntityOrDefault(entityType: EntityType): String {
        return entity.getOrDefault(entityType, entityType.name).toColorHex()
    }

    fun getSpawnerDisplayName(entityType: EntityType): String {
        return spawnerDisplayName.replace("{{entity}}", getEntityOrDefault(entityType)).toColorHex()
    }

    fun getSpawnerPlayerBreak(
        playerPseudo: String,
        entityType: EntityType,
        location: Location
    ): String {
        return spawnerPlayerBreak
            .replace("{{playerPseudo}}", playerPseudo)
            .replace("{{entity}}", getEntityOrDefault(entityType))
            .replace("{{x}}", location.x.toString())
            .replace("{{y}}", location.y.toString())
            .replace("{{z}}", location.z.toString())
            .toColorHex()
    }

    companion object {
        fun String.toColorHex(): String {
            var message = this
            val pattern: Pattern = Pattern.compile("#[a-fA-F0-9]{6}")
            var matcher: Matcher = pattern.matcher(message)
            while (matcher.find()) {
                val hexCode = message.substring(matcher.start(), matcher.end())
                val replaceSharp = hexCode.replace('#', 'x')
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
    val missingPermission: String
)
