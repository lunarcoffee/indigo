package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import dev.lunarcoffee.indigo.framework.core.commands.argparsers.QuotedArgumentParser
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer
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

        val commandName = content.substringBefore(' ').substringAfter(prefix)
        val command = bot.commandsByName[commandName] ?: return

        coroutineScope.launch { execute(command, event) }
    }

    override suspend fun execute(command: Command, event: GuildMessageReceivedEvent) {
        val stringArgs = QuotedArgumentParser(event.message.contentRaw).split().drop(1).toMutableList()
        val args = command.args.asList().map { (it as Transformer<*>).transform(stringArgs) }

        if (null in args || stringArgs.isNotEmpty())
            return

        val commandArgs = when (args.size) {
            0 -> Arg0()
            1 -> Arg1(args[0])
            2 -> Arg2(args[0], args[1])
            3 -> Arg3(args[0], args[1], args[2])
            4 -> Arg4(args[0], args[1], args[2], args[3])
            5 -> Arg5(args[0], args[1], args[2], args[3], args[4])
            else -> TODO()
        }

        val context = GuildCommandContext(bot, event)
        command.execute(context, commandArgs)
    }
}
