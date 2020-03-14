package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.commands.utility.reminders.Reminder
import dev.lunarcoffee.indigo.bot.util.guildsettings.GuildSettings
import dev.lunarcoffee.indigo.bot.util.zones.UserZone
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private val client = KMongo.createClient().coroutine
    private val db = client.getDatabase("Indigo-0")

    val prefixStore = db.getCollection<GuildSettings>("GuildSettingsStore")
    val zoneStore = db.getCollection<UserZone>("ZoneStore")
    val reminderStore = db.getCollection<Reminder>("ReminderStore")
}
