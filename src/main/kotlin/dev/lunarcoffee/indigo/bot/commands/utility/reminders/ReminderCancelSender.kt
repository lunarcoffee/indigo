package dev.lunarcoffee.indigo.bot.commands.utility.reminders

import dev.lunarcoffee.indigo.bot.commands.utility.remind.ReminderManager
import dev.lunarcoffee.indigo.bot.util.failure
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class ReminderCancelSender(private val userId: String, private val which: Int) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val reminders = ReminderManager.getReminders(userId)
        val toCancel = reminders.getOrNull(which - 1)

        if (toCancel == null || !ReminderManager.cancelReminder(toCancel.messageId))
            ctx.failure("That reminder does not exist!")
        else
            ctx.success("That reminder has been cancelled!")
    }
}
