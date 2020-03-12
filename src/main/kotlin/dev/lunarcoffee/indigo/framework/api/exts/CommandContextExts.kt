package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.services.paginators.Paginator
import dev.lunarcoffee.indigo.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Message

suspend fun CommandContext.send(paginator: Paginator): Message = paginator.start(this)
suspend fun CommandContext.send(sender: ContentSender) = sender.send(this)
