package dev.lunarcoffee.indigo.bot.commands.emote

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitByteArray
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
import dev.lunarcoffee.indigo.framework.api.exts.*
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Icon

@CommandGroup("Emote")
class EmoteCommands {
    fun emote() = command("emote") {
        description = """
            |`$name <emote names...>`
            |Sends custom emotes from servers I am in.
            |This command takes from one to twenty (inclusive) `emote names`. I will then try to find an emote with
            |each of those names, sending them. The purpose of this is to bypass server-specific emote usage, as any
            |server which I am in will open up its pool of emotes for use.
            |&{Conflicting names:}
            |If there is more than one emote with the same name, you can add a `~n` to the end of the name, where `n`
            |is an integer. This will make me get the `n`th emote I find with that name.
            |&{Example usage:}
            |- `$name omegalul`\n
            |- `$name pogchamp xd omegalul xd xd`\n
            |- `$name xd~1 xd~2`
        """.trimMargin()

        execute(TrRemaining) { (names) ->
            check(names, "I can only send up to 20 emotes!") { size > 20 } ?: return@execute

            val authorName = event.guild.getMember(event.author)!!.effectiveName
            val emotes = names
                .asSequence()
                .map { it.split('~') }
                .map { if (it.size > 1) Pair(it[0], it[1].toIntOrNull()) else Pair(it[0], 1) }
                .filter { it.second != null }
                .mapNotNull { (name, index) -> jda.getEmotesByName(name, true).getOrNull(index!! - 1) }
                .joinToString(" ") { it.asMention }

            check(emotes, "I can't access any of those emotes!") { isEmpty() } ?: return@execute

            runCatching { event.message.remove() }
            send("**$authorName**: $emotes")
        }
    }

    fun scan() = command("scan", "scanemotes") {
        description = """
            |`$name [message limit]`
            |Takes all custom emotes from the last few messages.
            |This command will find custom emotes from the last `message limit` messages. If a limit is not specified,
            |I will look in the last 100 messages. I will then PM you with the names and image links to each emote.
            |&{Example usage:}
            |- `$name`\n
            |- `$name 500`
        """.trimMargin()

        execute(TrInt.optional(100)) { (limit) ->
            check(limit, "I can only scan up to the last 1000 message!") { this !in 1..1_000 } ?: return@execute

            val emotes = event
                .channel
                .iterableHistory
                .take(limit + 1)
                .drop(1)
                .flatMap { it.emotes }
                .distinctBy { it.id }

            if (emotes.isEmpty()) {
                success("There were no emotes used in the last `$limit` messages!")
                return@execute
            }

            success("I am sending you your emotes!")

            val pmChannel = event.author.openPrivateChannel().await()
            pmChannel.send(
                pmChannel.paginator {
                    for (emotePage in emotes.chunked(16)) {
                        embedPage {
                            title = "${Emoji.DETECTIVE}  Here are your emotes:"
                            description = emotePage
                                .joinToString("\n") { "**${it.name}**: [image link](${it.imageUrl})" }
                        }
                    }
                }
            )
        }
    }

    fun steal() = command("steal", "stemt", "stealemote") {
        description = """
            |`$name [emote name]`
            |Gets and automatically adds an emote to your server.
            |This command will look for the specified emote with name `emote name`. If it is not specified, I will take
            |the last used emote in the current channel. I will then try to add it to the server. Note that I am only 
            |able to access emotes from servers I am in, or which have been recently used (what qualifies is loosely
            |defined).
            |&{Conflicting names:}
            |If there is more than one emote with the same `emote name`, you can add a `~n` to the end of the name, 
            |where `n` is an integer. This will make me get the `n`th emote I find with that name.
            |&{Example usage:}
            |- `$name`\n
            |- `$name myNiceEmote`
        """.trimMargin()

        execute(TrWord.optional()) { (name) ->
            checkPermission("You must be able to manage emotes!", Permission.MANAGE_EMOTES) ?: return@execute
            checkPermission("I must be able to manage emotes!", Permission.MANAGE_EMOTES, event.guild.selfMember)
                ?: return@execute

            val emote = if (name == null) {
                // Take the first emote in the first message with emotes in the last 100 messages.
                event.channel.iterableHistory.take(100).find { it.emotes.isNotEmpty() }?.emotes?.get(0).apply {
                    check(this, "There are no emotes in the last 100 messages!") { this == null } ?: return@execute
                }
            } else {
                val part = name.split('~')
                val (newName, index) = if (part.size > 1) Pair(part[0], part[1].toIntOrNull()) else Pair(part[0], 1)
                checkNull(index, "You provided an invalid index on the emote name!") ?: return@execute

                jda.getEmotesByName(newName, true).getOrNull(index!! - 1).apply {
                    checkNull(this, "I can't access that emote!") ?: return@execute
                }
            }

            val iconData = runCatching { Fuel.get(emote!!.imageUrl).timeout(3_000).awaitByteArray() }.getOrNull()
            checkNull(iconData, "Network request timed out. Discord or my connection is unstable.") ?: return@execute

            val add = runCatching { event.guild.createEmote(emote!!.name, Icon.from(iconData!!)).await() }.getOrNull()
            checkNull(add, "Your server is probably be out of emote slots!") ?: return@execute

            success("Your emote has been added!")
        }
    }
}
