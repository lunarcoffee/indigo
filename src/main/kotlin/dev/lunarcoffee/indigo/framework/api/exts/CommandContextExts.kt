package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

suspend fun CommandContext.send(sender: ContentSender) = sender.send(this)
