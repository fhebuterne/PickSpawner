package fr.fabienhebuterne.pickspawner.command.factory

data class CommandInfoInit(
    val abstractCommand: AbstractCommand
) {
    private val annotation: CommandInfo = abstractCommand.javaClass.getAnnotation(CommandInfo::class.java)
    var name: String = annotation.name
    var permission: String = annotation.permission
    var minimalRequiredArgs: Int = annotation.minimalRequiredArgs
    var usage: String = annotation.usage
}
