package dev.lunarcoffee.indigo.bot.listeners

import dev.lunarcoffee.indigo.bot.util.PrefixManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

@ListenerGroup
class PingListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        botId = event.jda.selfUser.id

        if (event.message.contentRaw in mentions) {
            runBlocking {
                val prefixes = PrefixManager.getPrefix(event.guild.id)
                val prefixString = prefixes.joinToString(", ") { "`$it`" }
                event.channel.success("My prefixes in this server are $prefixString!")
            }
        }
    }

    companion object {
        private lateinit var botId: String
        private val mentions by lazy { setOf("<@$botId>", "<@!$botId>") }
    }
}
