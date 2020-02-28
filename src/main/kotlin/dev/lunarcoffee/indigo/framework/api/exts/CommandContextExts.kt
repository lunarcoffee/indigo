package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed

suspend fun CommandContext.send(text: CharSequence) = sendMessage(text).await()
suspend fun CommandContext.send(message: Message) = sendMessage(message).await()
suspend fun CommandContext.send(embed: MessageEmbed) = sendMessage(embed).await()
