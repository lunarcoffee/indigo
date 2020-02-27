package dev.lunarcoffee.indigo.framework.core.bot

import dev.lunarcoffee.indigo.framework.core.commands.Command
import dev.lunarcoffee.indigo.framework.core.commands.CommandExecutor

interface CommandBot : ListenerBot {
    val commands: List<Command>
    val commandGroups: Map<String, List<Command>>
    val commandsByName: Map<String, Command>

    val commandExecutor: CommandExecutor
}
