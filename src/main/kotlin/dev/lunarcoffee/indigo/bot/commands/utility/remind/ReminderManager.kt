package dev.lunarcoffee.indigo.bot.commands.utility.remind

import dev.lunarcoffee.indigo.bot.util.Database
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
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
            Database.reminderStore.deleteOne(Reminder::messageId eq reminder.messageId)

            val guild = jda.getGuildById(reminder.guildId) ?: return@launch
            val author = guild.getMember(jda.getUserById(reminder.authorId) ?: return@launch) ?: return@launch
            val channel = guild.getTextChannelById(reminder.channelId) ?: return@launch

            // URL to jump to the original remind command invocation.
            val contextUrl = runCatching { channel.retrieveMessageById(reminder.messageId).await() }
                .getOrNull()
                ?.jumpUrl
                ?.run { "(link)[$this]" }
                ?: "(deleted)"

            channel.send(
                embed {
                    title = "${Emoji.ALARM_CLOCK}  Reminder for **${author.effectiveName}**:"
                    description = """
                        |**Mention**: ${author.asMention}
                        |**Context**: $contextUrl
                        |**Message**: ${reminder.message}
                    """.trimMargin()
                }
            )
        }
    }
}
