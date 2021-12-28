package fr.fabienhebuterne.pickspawner.commands.factory

annotation class CommandInfo(
    val name: String,
    val permission: String,
    val minimalRequiredArgs: Int,
    val usage: String
)
