package dev.lunarcoffee.indigo.bot.commands.restricted

import dev.lunarcoffee.indigo.bot.util.isAuthorOwner
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRestJoined
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrWord
import kotlin.system.exitProcess

@CommandGroup("Restricted")
class RestrictedCommands {
    fun kill() = command("kill", "shutdown") {
        description = """
            |`$name ["now"]`
            |Terminates the bot, either awaiting completion of or cancelling pending actions.
            |This command will terminate the bot. If `now` is specified, all pending actions will be cancelled. This is
            |a restricted command; only certain users can use it.
            |&{Example usage:}
            |- `$name`\n
            |- `$name now`
        """.trimMargin()

        execute(TrRestJoined.optional()) { (force) ->
            checkOwner() ?: return@execute

            success("Awaiting termination.")
            if (force == "now")
                bot.jda.shutdownNow()
            else
                bot.jda.shutdown()

            exitProcess(0)
        }
    }

    fun sMsg() = command("smsg", "sendmessage") {
        description = """
            |`$name <message>`
            |Makes me send the exact value of the given message.
            |This command will make me send the exact value of `message`. This is a restricted command; only certain
            |users can use it.
            |&{Example usage:}
            |- `$name hello, i am a robot`\n
            |- `$name **bold**`
        """.trimMargin()

        execute(TrRestJoined) { (message) -> send(message) }
    }

    fun sEbd() = command("sebd", "sendembed") {
        description = """
            |`$name <title> [description] [color hex code]`
            |Makes me send an embed with the specified attributes.
            |This command will make me send an embed with the specified `title`, `description`, and `color`. If left
            |unspecified, the description will be empty and the color will be the default scarlet color. This is a
            |restricted command; only certain users can use it.
            |&{Example usage:}
            |- `$name title description FF00000`\n
            |- `$name "the title"`
        """.trimMargin()

        execute(TrWord, TrWord.optional(""), TrWord.optional()) { (title, description, color) ->
            send(
                embed {
                    this@embed.title = title
                    this@embed.description = description

                    if (color != null)
                        this@embed.color = color.toIntOrNull(16) ?: this@embed.color
                }
            )
        }
    }

    private suspend fun GuildCommandContext.checkOwner(): Unit? {
        return check(this, "Only my owner can use that command!") { !isAuthorOwner(bot) }
    }
}
