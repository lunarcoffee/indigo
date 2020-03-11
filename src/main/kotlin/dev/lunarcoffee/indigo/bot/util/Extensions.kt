package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.consts.Emote
import dev.lunarcoffee.indigo.framework.api.exts.send
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

private val TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a")

suspend fun MessageChannel.success(message: String) = send("${Emoji.WHITE_CHECK_MARK}  **$message**")
suspend fun MessageChannel.failure(message: String) = send("${Emote(jda).error.asMention}  **$message**")

fun String.sanitize() = MarkdownSanitizer.sanitize(this)

fun ZonedDateTime.formatTimeOnly() = format(TIME_ONLY_FORMATTER)!!
