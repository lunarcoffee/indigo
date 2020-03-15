package dev.lunarcoffee.indigo.bot.listeners.starboard

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.*
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.guild.GenericGuildMessageEvent
import org.litote.kmongo.eq

object StarboardManager {
    suspend fun getStarboard(guildId: String) = GuildSettingsManager.get(guildId).starboard
    suspend fun getEntry(id: String) = Database.starboardStore.findOne(StarboardEntry::messageId eq id)

    fun countStars(message: Message) = message
        .reactions
        .find { it.reactionEmote.isEmoji && it.reactionEmote.emoji == Emoji.STAR }
        ?.count
        ?: 0

    suspend fun addOrUpdateEntry(event: GenericGuildMessageEvent) {
        val oldEntry = getEntry(event.messageId)

        if (oldEntry == null) {
            val entryId = sendEntry(event) ?: return
            val entry = StarboardEntry(event.messageId, entryId)
            Database.starboardStore.insertOne(entry)
        } else {
            val entry = event.channel.retrieveMessageById(oldEntry.entryId).await()
            entry.edit(getEntryEmbed(event.getMessage()))
        }
    }

    suspend fun removeEntry(event: GenericGuildMessageEvent) {
        val entry = getEntry(event.messageId) ?: return
        val message = event.channel.retrieveMessageById(entry.entryId).await()

        message.remove()
        Database.starboardStore.deleteOne(StarboardEntry::messageId eq event.messageId)
    }

    private suspend fun sendEntry(event: GenericGuildMessageEvent): String? {
        val starboard = getStarboard(event.guild.id)
        val message = event.getMessage()
        val channel = event.guild.getTextChannelById(starboard.channelId!!) ?: return null
        return channel.send(getEntryEmbed(message)).id
    }

    private fun getEntryEmbed(message: Message): MessageEmbed {
        return embed {
            field("Content", message.contentRaw.takeOrEllipsis(1_000))
            field {
                name = "Information"
                value = """
                    |**Author**: ${message.author.asMention}
                    |**Channel**: ${(message.channel as TextChannel).asMention}
                    |**Link**: [click here](${message.jumpUrl})
                """.trimMargin()
            }

            val stars = countStars(message)
            val plural = if (stars == 1) "" else "s"
            footer = "${Emoji.STAR} $stars star$plural!"
        }
    }
}
