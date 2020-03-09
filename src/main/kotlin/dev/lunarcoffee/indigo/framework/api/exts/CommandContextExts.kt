package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.services.paginators.Paginator
import net.dv8tion.jda.api.entities.Message

suspend fun CommandContext.send(paginator: Paginator): Message = paginator.start(this)
