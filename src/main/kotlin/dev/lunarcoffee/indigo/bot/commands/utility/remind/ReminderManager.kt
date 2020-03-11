package dev.lunarcoffee.indigo.bot.commands.utility.remind

import dev.lunarcoffee.indigo.bot.util.Database
import dev.lunarcoffee.indigo.framework.api.exts.send
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import org.litote.kmongo.eq
import java.time.ZonedDateTime

object ReminderManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun addReminder(reminder: Reminder, jda: JDA) {
        Database.reminderStore.insertOne(reminder)
        scheduleReminder(reminder, jda)
    }

    suspend fun scheduleReminder(reminder: Reminder, jda: JDA) {
        val timeDiffSeconds = reminder.time.toEpochSecond() - ZonedDateTime.now(reminder.time.zone).toEpochSecond()
        coroutineScope.launch {
            delay(timeDiffSeconds * 1_000L)
            jda.getGuildById(reminder.guildId)
                ?.getTextChannelById(reminder.channelId)
                ?.send(reminder.message) // TODO: send embed
            Database.reminderStore.deleteOne(Reminder::messageId eq reminder.messageId)
        }
    }
}
