package dev.lunarcoffee.indigo.bot.commands.utility.remind

import dev.lunarcoffee.indigo.bot.util.Database
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.sanitize
import dev.lunarcoffee.indigo.framework.api.dsl.message
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import kotlinx.coroutines.*
import net.dv8tion.jda.api.JDA
import org.litote.kmongo.eq
import java.time.ZonedDateTime

object ReminderManager {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getReminders(userId: String) = Database
        .reminderStore
        .find(Reminder::authorId eq userId)
        .toList()
        .sortedBy { it.time.toEpochSecond() }

    suspend fun reloadReminders(jda: JDA) {
        for (reminder in Database.reminderStore.find().toList())
            scheduleReminder(reminder, jda)
    }

    suspend fun addReminder(reminder: Reminder, jda: JDA) {
        Database.reminderStore.insertOne(reminder)
        scheduleReminder(reminder, jda)
    }

    suspend fun cancelReminder(messageId: String) =
        Database.reminderStore.deleteOne(Reminder::messageId eq messageId).deletedCount == 1L

    private suspend fun scheduleReminder(reminder: Reminder, jda: JDA) {
        val zoneTimeNow = ZonedDateTime.now(reminder.time.zone).toEpochSecond()
        val timeDiffSeconds = reminder.time.toEpochSecond() - zoneTimeNow

        coroutineScope.launch {
            delay(timeDiffSeconds * 1_000L)

            // Try to delete. If nothing is deleted, the reminder has been cancelled.
            val delete = Database.reminderStore.deleteOne(Reminder::messageId eq reminder.messageId)
            if (delete.deletedCount == 0L)
                return@launch

            val guild = jda.getGuildById(reminder.guildId) ?: return@launch
            val author = guild.getMember(jda.getUserById(reminder.authorId) ?: return@launch) ?: return@launch
            val channel = guild.getTextChannelById(reminder.channelId) ?: return@launch

            // URL to jump to the original remind command invocation.
            val contextUrl = runCatching { channel.retrieveMessageById(reminder.messageId).await() }
                .getOrNull()
                ?.jumpUrl
                ?.run { "[link]($this)" }
                ?: "(deleted)"

            channel.send(
                message {
                    content = author.asMention
                    embed {
                        title = "${Emoji.ALARM_CLOCK}  Reminder for **${author.effectiveName}**:"
                        description = """
                            |**Context**: $contextUrl
                            |**Message**: ${reminder.message.sanitize()}
                        """.trimMargin()
                    }
                }
            )
        }
    }
}
