package dev.lunarcoffee.indigo.bot.util.settings.guildsettings

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq

// Gets or inserts the default guild settings for the guild [guildId].
object GuildSettingsManager {
    suspend fun get(guildId: String) = Database.guildSettingsStore.findOne(GuildSettings::guildId eq guildId)
        ?: insertDefault(guildId)

    suspend fun update(settings: GuildSettings) {
        Database.guildSettingsStore.updateOne(
            GuildSettings::guildId eq settings.guildId,
            settings,
            UpdateOptions().upsert(true)
        )
    }

    private suspend fun insertDefault(guildId: String) =
        GuildSettings(guildId).apply { Database.guildSettingsStore.insertOne(this) }
}
