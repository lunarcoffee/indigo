package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.services.paginators.Paginator
import net.dv8tion.jda.api.entities.*

suspend fun MessageChannel.send(text: CharSequence): Message = sendMessage(text).await()
suspend fun MessageChannel.send(message: Message): Message = sendMessage(message).await()
suspend fun MessageChannel.send(embed: MessageEmbed): Message = sendMessage(embed).await()
suspend fun MessageChannel.send(paginator: Paginator): Message = paginator.start(this)
