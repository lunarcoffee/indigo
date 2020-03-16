package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.bot.commands.utility.remind.ReminderManager
import dev.lunarcoffee.indigo.bot.util.settings.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.framework.api.dsl.bot
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.Activity

fun main() = runBlocking {
    bot("src/main/resources/config.yaml") { customPrefix { GuildSettingsManager.get(it).prefixes } }.run {
        start()
        jda.presence.activity = Activity.watching("for a ping.")
        ReminderManager.reloadReminders(jda)
    }
}
