package dev.lunarcoffee.indigo.bot.listeners

import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
class CommandSuggestionListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {

    }
}
