package dev.lunarcoffee.indigo.framework.core.commands

import java.util.*

interface CommandExecutor : EventListener {
    suspend fun execute(command: Command, context: CommandContext)
}
