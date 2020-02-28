package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import java.util.*

interface CommandExecutor : EventListener {
    var bot: CommandBot

    suspend fun execute(command: Command, context: CommandContext)
}
