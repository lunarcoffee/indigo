package dev.lunarcoffee.indigo.framework.api.exts

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed

suspend fun MessageChannel.send(text: CharSequence): Message = sendMessage(text).await()
suspend fun MessageChannel.send(message: Message): Message = sendMessage(message).await()
suspend fun MessageChannel.send(embed: MessageEmbed): Message = sendMessage(embed).await()
