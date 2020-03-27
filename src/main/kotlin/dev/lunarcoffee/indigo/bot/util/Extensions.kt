package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.consts.Emote
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.bot.Bot
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.std.ClockTime
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent
import net.dv8tion.jda.api.utils.MarkdownSanitizer
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.min

private val DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu hh:mm:ss a")
private val TIME_ONLY_FORMATTER = DateTimeFormatter.ofPattern("hh:mm:ss a")

suspend fun MessageChannel.success(message: String) = send("${Emoji.WHITE_CHECK_MARK}  **$message**")
suspend fun MessageChannel.failure(message: String) = send("${Emote(jda).error.asMention}  **$message**")

suspend fun CommandContext.failureDefault(name: String) =
    failure("That's not right. Type `${invokedPrefix}help $name` for information.")

fun String.sanitize() = MarkdownSanitizer.sanitize(this)
fun String.takeOrEllipsis(limit: Int) = if (length > limit) "${take(40)}..." else this

fun ClockTime.toZoned(zone: ZoneId) = ZonedDateTime
    .now(zone)
    .truncatedTo(ChronoUnit.DAYS)
    .plusHours(hour.toLong() % 12)
    .plusMinutes(minute.toLong())
    .run { if (isPm) plusHours(12) else this }!!

fun ZonedDateTime.formatDefault() = format(DEFAULT_FORMATTER)!!
fun ZonedDateTime.formatTimeOnly() = format(TIME_ONLY_FORMATTER)!!
fun LocalDateTime.formatDefault() = format(DEFAULT_FORMATTER)!!
fun LocalDateTime.formatTimeOnly() = format(TIME_ONLY_FORMATTER)!!
fun OffsetDateTime.formatDefault() = format(DEFAULT_FORMATTER)!!
fun OffsetDateTime.formatTimeOnly() = format(TIME_ONLY_FORMATTER)!!

fun List<*>.ifEmptyNone() = ifEmpty { "(none)" }.toString()
fun String.ifEmptyNone() = ifEmpty { "(none)" }
fun <T> T?.ifNullNone() = this?.toString() ?: "(none)"
fun String?.ifNullNoneElseLink() = this?.run { "[link]($this)" }.ifNullNone()
fun Boolean.toYesNo() = if (this) "yes" else "no"

fun GuildCommandContext.isAuthorOwner(bot: Bot) = event.author.id == bot.config["ownerId"]!!

suspend fun GenericGuildMessageEvent.getMessage(): Message = channel.retrieveMessageById(messageId).await()

fun String.distance(other: String): Int {
    val lhsLength = length
    val rhsLength = other.length

    var cost = Array(lhsLength) { it }
    var newCost = Array(lhsLength) { 0 }

    for (i in 1 until rhsLength) {
        newCost[0] = i
        for (j in 1 until lhsLength) {
            val match = if (this[j - 1] == other[i - 1]) 0 else 1
            newCost[j] = min(min(cost[j] + 1, newCost[j - 1] + 1), cost[j - 1] + match)
        }
        cost = newCost.also { newCost = cost }
    }
    return cost.last()
}
