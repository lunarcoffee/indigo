package dev.lunarcoffee.indigo.bot.commands.owner

import dev.lunarcoffee.indigo.bot.util.isAuthorOwner
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRestJoined
import kotlin.system.exitProcess

@CommandGroup("Restricted")
class RestrictedCommands {
    fun kill() = command("kill", "shutdown") {
        description = """
            |`$name ["now"]`
            |Terminates the bot, either awaiting completion of or cancelling pending actions.
            |This command will terminate the bot. If `now` is specified, all pending actions will be cancelled.
            |&{Example usage:}
            |- `kill`\n
            |- `kill now`
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

    private suspend fun GuildCommandContext.checkOwner(): Unit? {
        return check(this, "Only my owner can use that command!") { !isAuthorOwner(bot) }
    }
}
