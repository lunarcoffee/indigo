package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.consts.Emote
import dev.lunarcoffee.indigo.framework.api.exts.send
import net.dv8tion.jda.api.entities.MessageChannel

suspend fun MessageChannel.success(message: String) = send("${Emoji.WHITE_CHECK_MARK}  **$message**")
suspend fun MessageChannel.failure(message: String) = send("${Emote(
    jda
).error.asMention}  **$message**")
