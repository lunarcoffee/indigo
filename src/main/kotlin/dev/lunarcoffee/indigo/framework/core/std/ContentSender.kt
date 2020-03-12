package dev.lunarcoffee.indigo.framework.core.std

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

interface ContentSender {
    suspend fun send(ctx: CommandContext)
}
