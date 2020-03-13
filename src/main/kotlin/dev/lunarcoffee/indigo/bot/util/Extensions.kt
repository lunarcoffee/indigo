package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.consts.Emote
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.std.ClockTime
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a")

suspend fun MessageChannel.success(message: String) = send("${Emoji.WHITE_CHECK_MARK}  **$message**")
suspend fun MessageChannel.failure(message: String) = send("${Emote(jda).error.asMention}  **$message**")

fun String.sanitize() = MarkdownSanitizer.sanitize(this)

fun ClockTime.toZoned(zone: ZoneId) = ZonedDateTime
    .now(zone)
    .truncatedTo(ChronoUnit.DAYS)
    .plusHours(hour.toLong() % 12)
    .plusMinutes(minute.toLong())
    .run { if (isPm) plusHours(12) else this }!!

fun ZonedDateTime.formatTimeOnly() = format(TIME_ONLY_FORMATTER)!!

fun List<*>.ifEmptyNone() = ifEmpty { "(none)" }.toString()
