package dev.lunarcoffee.indigo.bot.util.settings.usersettings

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq
import java.time.ZoneId

object UserSettingsManager {
    suspend fun get(userId: String) = Database.userSettingsStore.findOne(UserSettings::userId eq userId)
        ?: insertDefault(userId)

    suspend fun update(userId: String, newZone: ZoneId? = null) {
        val new = get(userId).apply { zone = newZone }
        Database.userSettingsStore.updateOne(UserSettings::userId eq userId, new, UpdateOptions().upsert(true))
    }

    private suspend fun insertDefault(userId: String) =
        UserSettings(userId).apply { Database.userSettingsStore.insertOne(this) }
}
