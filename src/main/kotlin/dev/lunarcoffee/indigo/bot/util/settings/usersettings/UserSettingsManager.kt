package dev.lunarcoffee.indigo.bot.util.settings.usersettings

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq

object UserSettingsManager {
    suspend fun get(userId: String) = Database.userSettingsStore.findOne(UserSettings::userId eq userId)
        ?: insertDefault(userId)

    suspend fun update(settings: UserSettings) {
        Database.userSettingsStore.updateOne(
            UserSettings::userId eq settings.userId,
            settings,
            UpdateOptions().upsert(true)
        )
    }

    private suspend fun insertDefault(userId: String) =
        UserSettings(userId).apply { Database.userSettingsStore.insertOne(this) }
}
