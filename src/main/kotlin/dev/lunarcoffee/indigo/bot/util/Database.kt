package dev.lunarcoffee.indigo.bot.util

import dev.lunarcoffee.indigo.bot.commands.utility.remind.Reminder
import dev.lunarcoffee.indigo.bot.listeners.starboard.StarboardEntry
import dev.lunarcoffee.indigo.bot.util.settings.guildsettings.GuildSettings
import dev.lunarcoffee.indigo.bot.util.settings.usersettings.UserSettings
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private val client = KMongo.createClient().coroutine
    private val db = client.getDatabase("Indigo-0")

    val guildSettingsStore = getCol<GuildSettings>("GuildSettings")
    val userSettingsStore = getCol<UserSettings>("UserSettings")
    val reminderStore = getCol<Reminder>("Reminder")
    val starboardStore = getCol<StarboardEntry>("StarboardEntry")

    private inline fun <reified T : Any> getCol(name: String) = db.getCollection<T>("${name}Store")
}
