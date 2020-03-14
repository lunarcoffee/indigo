package dev.lunarcoffee.indigo.bot.commands.utility

import dev.lunarcoffee.indigo.bot.commands.utility.help.CommandHelpSender
import dev.lunarcoffee.indigo.bot.commands.utility.help.ListHelpSender
import dev.lunarcoffee.indigo.bot.commands.utility.reminders.Reminder
import dev.lunarcoffee.indigo.bot.commands.utility.reminders.ReminderManager
import dev.lunarcoffee.indigo.bot.commands.utility.reminders.remindl.ReminderCancelSender
import dev.lunarcoffee.indigo.bot.commands.utility.reminders.remindl.ReminderListSender
import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.zones.ZoneManager
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.remove
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import java.time.ZoneId
import java.time.ZonedDateTime

@CommandGroup("Utility")
class UtilityCommands {
    fun remindIn() = command("remindin") {
        description = """
            |`$name <delay> [message]`
            |Sets a reminder to fire off and ping you in some amount of time.
            |This command takes a `delay` time string and an optional `message`. After the amount of time specified, I
            |will ping you with your message. The time string is formatted specifically like `3h 30m` or similar (look
            |at the example usages below), and the message can be at most 500 characters.
            |&{Example usage:}
            |- `remindin 10h thaw the meat for dinner`\n
            |- `remindin 1d 30m 30s hello me`\n
            |- `remindin 20m`
        """.trimMargin()

        execute(TrTime, TrRestJoined.optional("(no message)")) { (delay, message) ->
            check(message, "Your message can be at most 500 characters!") { length > 500 } ?: return@execute

            val timeAfter = delay.asTimeFromNow(ZoneId.systemDefault())
            val timeString = timeAfter.formatDefault()

            val reminder = event
                .run { Reminder(message, timeAfter, timeString, guild.id, channel.id, messageId, author.id) }
            ReminderManager.addReminder(reminder, jda)

            success("I will remind you in `$delay`!")
        }
    }

    fun remindAt() = command("remindat") {
        description = """
            |`$name <clock time> [message]` 
            |Sets a reminder to fire off and ping you at some later time today.
            |This command is meant to be more convenient than `remindin` for reminders set for the same day. This
            |command needs a `clock time`, which is formatted specifically like `1:43pm` is. The am/pm must be present,
            |and there cannot be spaces. The `message` comes after and is optional, and can be anything you want within
            |500 characters.
            |&{Example usage:}
            |- `remindat 12:00pm bake cookies`\n
            |- `remindat 6:45pm stop playing league and study`\n
            |- `remindat 2:00am`
        """.trimMargin()

        execute(TrClockTime, TrRestJoined.optional("(no message)")) { (clockTime, message) ->
            val zone = ZoneManager.getZone(event.author.id)
            check(zone, "You must set a timezone with the `settz` command!") { this == null } ?: return@execute
            check(message, "Your message can be at most 500 characters!") { length > 500 } ?: return@execute

            val timeAfter = clockTime.toZoned(zone!!)
            check(timeAfter, "That time has already passed!") { isBefore(ZonedDateTime.now(zone)) } ?: return@execute

            val timeString = timeAfter.formatDefault()
            val reminder = event
                .run { Reminder(message, timeAfter, timeString, guild.id, channel.id, messageId, author.id) }
            ReminderManager.addReminder(reminder, jda)

            success("I will remind you at `${timeAfter.formatTimeOnly()}`!")
        }
    }

    fun remindl() = command("remindl", "reminders") {
        description = """
            |`$name ["cancel" which]`
            |Shows your reminders or cancels one of them.
            |This command used without arguments (see the first example usage) will list each pending reminder you 
            |have along with a number. If `cancel` and `which` are specified, I will cancel the reminder with number
            |equal to `which`.
            |&{Example usage:}
            |- `remindl`\n
            |- `remindl cancel 2`
        """.trimMargin()

        execute(TrRemaining.optional()) { (cancel) ->
            if (cancel == null)
                send(ReminderListSender(event.author.id))
            else if (cancel.size == 2 && cancel[0] == "cancel" && cancel[1].toIntOrNull() != null)
                send(ReminderCancelSender(event.author.id, cancel[1].toInt()))
        }
    }

    fun emote() = command("emote") {
        description = """
            |`$name <emote names...>`
            |Sends custom emotes from servers I am in.
            |This command takes from one to twenty (inclusive) `emote names`. I will then try to find an emote with
            |each of those names, sending them. The purpose of this is to bypass server-specific emote usage, as any
            |server which I am in will open up its pool of emotes for use.
            |&{Conflicting names:}
            |If there is more than one emote with the same name, you can add a `~n` to the end of the name, where `n`
            |is an integer. This will make me get the `n`th emote I find with that name.
            |&{Example usage:}
            |- `emote omegalul`\n
            |- `emote pogchamp xd omegalul xd xd`\n
            |- `emote xd~1 xd~2`
        """.trimMargin()

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

    fun help() = command("help") {
        description = """
            |`$name [command name]`
            |Shows help text about commands and examples of using them.
            |This command, without a `command name`, will show all my command groups with their commands. With a name,
            |this will show information about the command, including its aliases (names which you can use in place of
            |the normal name), short description, usage string, and extended description.
            |&{Reading usage strings:}
            |Usually, the example usages are good enough, but for exactly how you can use a command, the usage string
            |can help. Each usage string will start with the command name, then have a list of arguments. These
            |arguments are usually just formatted strings of text that mean something, like a number or name. These
            |characters have special meaning:\n
            |- `<arg>`: required argument\n
            |- `[arg]`: optional argument\n
            |- `arg...`: one or more of the argument\n
            |- `"arg"`: that specific string of text within the quotes\n
            |These can be combined, as with `[arg...]` (one or more of an optional argument), for instance.
            |&{Example usage:}
            |- `help ping`\n
            |- `help`
        """.trimMargin()

        execute(TrWord.optional()) { (commandName) ->
            send(
                if (commandName == null) {
                    ListHelpSender(bot)
                } else {
                    val command = bot.commandsByName[commandName]
                    check(command, "I can't find that command!") { this == null } ?: return@execute
                    CommandHelpSender(command!!)
                }
            )
        }
    }
}
