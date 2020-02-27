package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import dev.lunarcoffee.indigo.framework.core.commands.argparsers.QuotedArgumentParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

// [prefix] takes a guild ID and returns the prefix for that guild.
class GuildCommandExecutor(private val prefix: (String) -> String) : CommandExecutor, ListenerAdapter() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override lateinit var bot: CommandBot

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw
        val prefix = prefix(event.guild.id)

        if (event.author.isBot || event.isWebhookMessage || !content.startsWith(prefix))
            return

        val commandName = content.split("""\s+""".toRegex())[0].substringAfter(prefix) // TODO: proper parsing logic
        val command = bot.commandsByName[commandName] ?: return

        coroutineScope.launch { execute(command, event) }
    }

    override suspend fun execute(command: Command, event: GuildMessageReceivedEvent) {
        val args = QuotedArgumentParser(event.message.contentRaw).split().drop(1)

    }
}
