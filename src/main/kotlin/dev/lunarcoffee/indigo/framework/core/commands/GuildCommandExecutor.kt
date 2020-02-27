package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.commands.argparsers.QuotedArgumentParser
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

// [prefix] takes a guild ID and returns the prefix for that guild.
class GuildCommandExecutor(private val prefix: (String) -> String) : CommandExecutor, ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw
        val prefix = prefix(event.guild.id)

        if (event.author.isBot || event.isWebhookMessage || !content.startsWith(prefix))
            return

        val commandName = content.split("""\s+""".toRegex())[0].substringAfter(prefix) // TODO: proper parsing logic
        val command = GuildCommand("", listOf(), null) {}
        execute(command, event)
    }

    override fun execute(command: Command, event: GuildMessageReceivedEvent) {
        val args = QuotedArgumentParser(event.message.contentRaw).split().drop(1)
        println(args)
    }
}
