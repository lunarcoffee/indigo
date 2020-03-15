package dev.lunarcoffee.indigo.bot.util.guildsettings

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq

// Gets or inserts the default guild settings for the guild [guildId].
object GuildSettingsManager {
    suspend fun get(guildId: String) = Database.prefixStore.findOne(GuildSettings::guildId eq guildId)
        ?: insertDefault(guildId)

    suspend fun update(
        guildId: String,
        newPrefixes: List<String>? = null,
        newConfigRoleId: String? = null,
        newStarboard: StarboardSettings? = null
    ): GuildSettings {

        val settings = get(guildId).apply {
            prefixes = newPrefixes ?: prefixes
            configRoleId = newConfigRoleId ?: configRoleId
            starboard = newStarboard ?: starboard
        }

        Database.prefixStore.updateOne(GuildSettings::guildId eq guildId, settings, UpdateOptions().upsert(true))
        return settings
    }

    private suspend fun insertDefault(guildId: String) =
        GuildSettings(guildId).apply { Database.prefixStore.insertOne(this) }
}
