package dev.lunarcoffee.indigo.bot.util.zones

import com.mongodb.client.model.UpdateOptions
import dev.lunarcoffee.indigo.bot.util.Database
import org.litote.kmongo.eq
import java.time.ZoneId

object ZoneManager {
    suspend fun getZone(userId: String) = Database
        .zoneStore
        .findOne(UserZone::userId eq userId)
        ?.zone

    // Mainly used in commands to check if a user has a timezone associated with them.
    suspend fun isSet(userId: String) = getZone(userId) != null

    suspend fun setZone(userId: String, zone: ZoneId) {
        val userZone = UserZone(userId, zone)
        Database.zoneStore.updateOne(UserZone::userId eq userId, userZone, UpdateOptions().upsert(true))
    }
}
