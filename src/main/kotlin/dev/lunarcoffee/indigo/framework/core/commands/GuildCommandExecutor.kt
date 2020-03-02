package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import dev.lunarcoffee.indigo.framework.core.commands.argparsers.QuotedArgumentParser
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

// [prefix] takes a guild ID and returns the valid prefixes for that guild.
class GuildCommandExecutor(private val prefix: (String) -> List<String>) : CommandExecutor, ListenerAdapter() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override lateinit var bot: CommandBot

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val content = event.message.contentRaw
        val prefixes = prefix(event.guild.id)
        val invokedPrefix = prefixes.find { content.startsWith(it) } ?: return

        if (event.author.isBot || event.isWebhookMessage)
            return

        val commandName = content.substringBefore(' ').substringAfter(invokedPrefix)
        val command = bot.commandsByName[commandName] ?: return
        val context = GuildCommandContext(bot, invokedPrefix, event)

        coroutineScope.launch { execute(command, context) } // TODO: catch exceptions
    }

    override suspend fun execute(command: Command, context: CommandContext) {
        context as GuildCommandContext

        val stringArgs = QuotedArgumentParser(context.event.message.contentRaw).split().toMutableList()
        val argsOptional = command
            .args
            .asList()
            .map { (it as Transformer<*>).transform(stringArgs) ?: if (it.isOptional) Unit else null }

        if (null in argsOptional || stringArgs.isNotEmpty())
            return

        val args = argsOptional.map { if (it == Unit) null else it }
        val commandArgs = when (args.size) {
            0 -> Arg0
            1 -> Arg1(args[0])
            2 -> Arg2(args[0], args[1])
            3 -> Arg3(args[0], args[1], args[2])
            4 -> Arg4(args[0], args[1], args[2], args[3])
            5 -> Arg5(args[0], args[1], args[2], args[3], args[4])
            6 -> Arg6(args[0], args[1], args[2], args[3], args[4], args[5])
            else -> ArgUnchecked(args)
        }

        command.execute(context, commandArgs)
    }
}
