package fr.fabienhebuterne.pickspawner.config

data class MigrationPickaxeConfig(
    val enabled: Boolean = false,
    val pickaxeName: String? = null,
    val pickaxeLore: List<String> = listOf(),
    val loreToExclude: String? = null
): ConfigType
