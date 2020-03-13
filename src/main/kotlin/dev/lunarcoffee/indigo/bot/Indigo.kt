package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.bot.commands.utility.remind.ReminderManager
import dev.lunarcoffee.indigo.bot.util.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.framework.api.dsl.bot
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    bot("src/main/resources/config.yaml") { customPrefix { GuildSettingsManager.get(it).prefixes } }.run {
        start()
        ReminderManager.reloadReminders(jda)
    }
}
