package dev.lunarcoffee.indigo.bot.listeners

import dev.lunarcoffee.indigo.bot.util.distance
import dev.lunarcoffee.indigo.bot.util.failure
import dev.lunarcoffee.indigo.bot.util.settings.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.framework.core.bot.GuildCommandBot
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.ListenerGroup
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

@ListenerGroup
class CommandSuggestionListener : ListenerAdapter() {
    private val userSuggestions = ConcurrentHashMap<String, Pair<String, GuildMessageReceivedEvent>>()
    private val bot by lazy { GuildCommandBot.instance }

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) = runBlocking {
        val prefixes = GuildSettingsManager.get(event.guild.id).prefixes
        if (event.message.contentRaw.equals(RERUN_TEXT, true))
            handleRerun(event.author.id, prefixes)
        else
            handleCommandSuggestion(event, prefixes)
    }

    private suspend fun handleCommandSuggestion(event: GuildMessageReceivedEvent, prefixes: List<String>) {
        userSuggestions -= event.author.id

        val content = event.message.contentRaw
        val invokedPrefix = prefixes.find { content.startsWith(it) } ?: return

        val inputCommandName = content.substringBefore(' ').substringAfter(invokedPrefix)
        val (distance, command) = bot
            .commandsByName
            .keys
            .sortedBy { abs(inputCommandName[0] - it[0]) }
            .map { Pair(inputCommandName.distance(it), it) }
            .minBy { it.first }!!

        // The command invocation is correct, no need for a suggestion.
        if (distance == 0)
            return

        if (distance < 4) {
            userSuggestions[event.author.id] = Pair(command, event)
            event.channel.failure("No command has that name. If you meant `$command`, type `$RERUN_TEXT` to run it.")
        }
    }

    private suspend fun handleRerun(userId: String, prefixes: List<String>) {
        val (commandName, event) = userSuggestions[userId] ?: return
        val command = bot.commandsByName[commandName] ?: return
        val context = GuildCommandContext(bot, prefixes[0], event)

        bot.commandExecutor.execute(command, context)
        userSuggestions -= userId
    }

    companion object {
        private const val RERUN_TEXT = "ok"
    }
}
