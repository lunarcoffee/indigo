package dev.lunarcoffee.indigo.bot.util

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.eq

// Gets or inserts the default prefixes for the guild [guildId].
object PrefixManager {
    suspend fun getPrefix(guildId: String) = Database.prefixStore.findOne(PrefixPair::guildId eq guildId)?.prefixes
        ?: setPrefix(guildId, listOf(".."))

    suspend fun setPrefix(guildId: String, prefixes: List<String>): List<String> {
        val prefixPair = PrefixPair(guildId, prefixes)
        Database.prefixStore.updateOne(PrefixPair::guildId eq guildId, prefixPair, UpdateOptions().upsert(true))
        return prefixes
    }
}
