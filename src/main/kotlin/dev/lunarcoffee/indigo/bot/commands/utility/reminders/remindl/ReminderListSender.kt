package dev.lunarcoffee.indigo.bot.commands.utility.reminders.remindl

import dev.lunarcoffee.indigo.bot.commands.utility.reminders.ReminderManager
import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class ReminderListSender(private val userId: String) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val reminders = ReminderManager.getReminders(userId)

        if (reminders.isEmpty()) {
            ctx.success("You have no pending reminders!")
            return
        }

        ctx.send(
            ctx.paginator {
                var which = 1
                for (reminderPage in reminders.chunked(16)) {
                    embedPage {
                        title = "${Emoji.ALARM_CLOCK}  Your pending reminders:"

                        for (reminder in reminderPage) {
                            description += "**#${which++}**: "
                            description += "`${reminder.message.takeOrEllipsis(40).sanitize()}` at "
                            description += "${reminder.timeString}\n"
                        }
                    }
                }
            }
        )
    }
}
