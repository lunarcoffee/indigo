package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
class PingListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.message.contentRaw == "b")
            event.channel.sendMessage("B").queue()
    }
}
