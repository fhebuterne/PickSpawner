package fr.fabienhebuterne.pickspawner.command.factory

annotation class CommandInfo(
    val name: String,
    val permission: String,
    val minimalRequiredArgs: Int,
    val usage: String
)
