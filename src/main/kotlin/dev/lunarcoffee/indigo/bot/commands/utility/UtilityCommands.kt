package dev.lunarcoffee.indigo.bot.commands.utility

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.remove
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*

@CommandGroup("Utility")
class UtilityCommands {
    fun remind() = command("remind") {
        description = "Sets a reminder to fire off and ping you later some time."

        execute(TrTime, TrRestJoined) { (delay, message) ->

        }
    }

    fun emote() = command("emote") {
        description = "Sends custom emotes from any of the servers I am in."

        execute(TrRemaining) { (names) ->
            check(names, "I can only send up to 20 emotes!") { size > 20 } ?: return@execute

            val authorName = event.guild.getMember(event.author)!!.effectiveName
            val emotes = names
                .asSequence()
                .map { it.split('~') }
                .map { if (it.size > 1) Pair(it[0], it[1].toIntOrNull()) else Pair(it[0], 1) }
                .filter { it.second != null }
                .onEach { println(it) }
                .mapNotNull { (name, index) -> jda.getEmotesByName(name, true).getOrNull(index!! - 1) }
                .joinToString(" ") { it.asMention }

            check(emotes, "I can't access any of those emotes!") { isEmpty() } ?: return@execute

            runCatching { event.message.remove() }
            send("**$authorName**: $emotes")
        }
    }
}
