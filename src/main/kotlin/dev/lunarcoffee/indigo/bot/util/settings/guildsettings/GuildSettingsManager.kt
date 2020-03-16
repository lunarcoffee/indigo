package dev.lunarcoffee.indigo.bot.util.settings.guildsettings

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq

// Gets or inserts the default guild settings for the guild [guildId].
object GuildSettingsManager {
    suspend fun get(guildId: String) = Database.guildSettingsStore.findOne(GuildSettings::guildId eq guildId)
        ?: insertDefault(guildId)

    suspend fun update(
        guildId: String,
        newPrefixes: List<String>? = null,
        newConfigRoleId: String? = null,
        newStarboard: StarboardSettings? = null
    ) {
        val new = get(guildId).apply {
            prefixes = newPrefixes ?: prefixes
            configRoleId = newConfigRoleId
            starboard = newStarboard ?: starboard
        }
        Database.guildSettingsStore.updateOne(GuildSettings::guildId eq guildId, new, UpdateOptions().upsert(true))
    }

    private suspend fun insertDefault(guildId: String) =
        GuildSettings(guildId).apply { Database.guildSettingsStore.insertOne(this) }
}
