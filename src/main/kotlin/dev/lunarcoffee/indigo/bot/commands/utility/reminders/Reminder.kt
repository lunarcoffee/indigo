package dev.lunarcoffee.indigo.bot.commands.utility.reminders

import java.time.ZonedDateTime

class Reminder(
    val message: String,
    val time: ZonedDateTime,
    val timeString: String,
    val guildId: String,
    val channelId: String,
    val messageId: String,
    val authorId: String
)
