package dev.lunarcoffee.indigo.bot.commands.utility

import dev.lunarcoffee.indigo.bot.commands.utility.remind.Reminder
import dev.lunarcoffee.indigo.bot.commands.utility.remind.ReminderManager
import dev.lunarcoffee.indigo.bot.util.formatTimeOnly
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.bot.util.zones.ZoneManager
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.remove
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import java.time.ZoneId

@CommandGroup("Utility")
class UtilityCommands {
    fun remindIn() = command("remindin") {
        description = "Sets a reminder to fire off and ping in some amount of time."

        execute(TrTime, TrRestJoined) { (delay, message) ->
            check(message, "Your message can be at most 500 characters!") { length > 500 } ?: return@execute

//            val zone = ZoneManager.getZone(event.author.id)
            val timeAfter = delay.asTimeFromNow(ZoneId.systemDefault())
            val reminder = event.run { Reminder(message, timeAfter, guild.id, channel.id, messageId, author.id) }

            ReminderManager.addReminder(reminder, jda)

//            val timeString = if (zone != null) " at `${timeAfter.formatTimeOnly()}" else ""
            success("I will remind you in `$delay`!")
        }
    }

    fun remindAt() = command("remindat") {
        description = "Sets a reminder to fire off and ping you at some later time."

        execute(TrClockTime, TrRestJoined) { (delay, message) ->

        }
    }

    fun emote() = command("emote") {
        description = "Sends custom emotes from any of the servers I am in."

        execute(TrRemaining) { (names) ->
            check(names, "I can only send up to 20 emotes!") { size > 20 } ?: return@execute

            val authorName = event.guild.getMember(event.author)!!.effectiveName
            val emotes = names
                .asSequence()
                .map { it.split('~') }
                .map { if (it.size > 1) Pair(it[0], it[1].toIntOrNull()) else Pair(it[0], 1) }
                .filter { it.second != null }
                .mapNotNull { (name, index) -> jda.getEmotesByName(name, true).getOrNull(index!! - 1) }
                .joinToString(" ") { it.asMention }

            check(emotes, "I can't access any of those emotes!") { isEmpty() } ?: return@execute

            runCatching { event.message.remove() }
            send("**$authorName**: $emotes")
        }
    }
}
