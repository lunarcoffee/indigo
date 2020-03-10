package dev.lunarcoffee.indigo.bot.commands.utility

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRemaining
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRestJoined
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrTime

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
            val authorName = event.guild.getMember(event.author)!!.effectiveName
            val emotes = names
                .map { it.split('~') }
                .map { if (it.size > 1) Pair(it[0], it[1].toIntOrNull()) else Pair(it[0], 0) }
                .filter { it.second == null }
                .mapNotNull { (name, index) -> jda.getEmotesByName(name, true).getOrNull(index!!) }
                .joinToString(" ") { it.asMention }

            send("**$authorName**: $emotes")
        }
    }
}
