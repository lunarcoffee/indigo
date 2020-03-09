package dev.lunarcoffee.indigo.bot.commands.config

import dev.lunarcoffee.indigo.bot.util.PrefixManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRemaining

@CommandGroup("Config")
class ConfigCommands {
    fun setPrefix() = command("setprefix") {
        description = "Sets the accepted bot prefixes for this server, overriding the previous prefixes."

        execute(TrRemaining) { (prefixes) ->
            check(prefixes, "I can't have more than 10 prefixes!") { size > 10 }
            check(prefixes, "Prefixes must be 1 to 5 characters long (inclusive)!") { any { it.length !in 1..5 } }
            check(prefixes, "Prefixes may not contain spaces!") { any { ' ' in it } }

            if (checkFailed)
                return@execute

            PrefixManager.setPrefix(guild.id, prefixes.distinct())
            success("My prefixes for this server have been updated!")
        }
    }
}
