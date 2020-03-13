package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.bot.commands.utility.remind.ReminderManager
import dev.lunarcoffee.indigo.bot.util.prefixes.PrefixManager
import dev.lunarcoffee.indigo.framework.api.dsl.bot
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    bot("src/main/resources/config.yaml") { customPrefix { PrefixManager.getPrefix(it) } }.run {
        start()
        ReminderManager.reloadReminders(jda)
    }
}
