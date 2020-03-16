package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.commands.utility.reminders.Reminder
import dev.lunarcoffee.indigo.bot.listeners.starboard.StarboardEntry
import dev.lunarcoffee.indigo.bot.util.settings.guildsettings.GuildSettings
import dev.lunarcoffee.indigo.bot.util.settings.usersettings.UserSettings
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private val client = KMongo.createClient().coroutine
    private val db = client.getDatabase("Indigo-0")

    val guildSettingsStore = db.getCollection<GuildSettings>("PrefixStore")
    val userSettingsStore = db.getCollection<UserSettings>("ZoneStore")
    val reminderStore = db.getCollection<Reminder>("ReminderStore")
    val starboardStore = db.getCollection<StarboardEntry>("StarboardEntryStore")
}
