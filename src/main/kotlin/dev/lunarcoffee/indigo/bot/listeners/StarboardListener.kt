package dev.lunarcoffee.indigo.bot.listeners

import dev.lunarcoffee.indigo.bot.listeners.starboard.StarboardManager
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.getMessage
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.MessageReaction
import net.dv8tion.jda.api.events.message.guild.*
import net.dv8tion.jda.api.events.message.guild.react.*
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
class StarboardListener : ListenerAdapter() {
    override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) = runBlocking {
        val starboard = StarboardManager.getStarboard(event.guild.id)
        if (!starboard.enabled || !isStarEmoji(event.reactionEmote))
            return@runBlocking

        val message = event.getMessage()
        if (event.userId == message.author.id) {
            event.reaction.removeReaction(event.user).await()
            return@runBlocking
        }

        val starCount = StarboardManager.countStars(message)
        if (starCount >= starboard.threshold)
            StarboardManager.addOrUpdateEntry(event)
    }

    override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) = runBlocking {
        val starboard = StarboardManager.getStarboard(event.guild.id)
        if (!starboard.enabled || !isStarEmoji(event.reactionEmote))
            return@runBlocking

        val starCount = StarboardManager.countStars(event.getMessage())
        if (starCount >= starboard.threshold)
            return@runBlocking StarboardManager.addOrUpdateEntry(event)
        StarboardManager.removeEntry(event)
    }

    override fun onGuildMessageReactionRemoveAll(event: GuildMessageReactionRemoveAllEvent) = runBlocking {
        starboardEnabledOrNull(event) ?: return@runBlocking
        StarboardManager.removeEntry(event)
    }

    override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) = runBlocking {
        starboardEnabledOrNull(event) ?: return@runBlocking
        StarboardManager.getEntry(event.messageId) ?: return@runBlocking
        StarboardManager.addOrUpdateEntry(event)
    }

    override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) = runBlocking {
        starboardEnabledOrNull(event) ?: return@runBlocking
        StarboardManager.removeEntry(event)
    }

    private suspend fun starboardEnabledOrNull(event: GenericGuildMessageEvent) =
        StarboardManager.getStarboard(event.guild.id).enabled.takeIf { true }

    private fun isStarEmoji(reaction: MessageReaction.ReactionEmote) = reaction.isEmoji && reaction.emoji == Emoji.STAR
}
