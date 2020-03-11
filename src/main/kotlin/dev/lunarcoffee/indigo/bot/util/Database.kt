package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.commands.utility.remind.Reminder
import dev.lunarcoffee.indigo.bot.util.prefixes.PrefixPair
import dev.lunarcoffee.indigo.bot.util.zones.UserZone
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private val client = KMongo.createClient().coroutine
    private val db = client.getDatabase("Indigo-0")

    val prefixStore = db.getCollection<PrefixPair>("PrefixStore")
    val zoneStore = db.getCollection<UserZone>("ZoneStore")
    val reminderStore = db.getCollection<Reminder>("ReminderStore")
}
