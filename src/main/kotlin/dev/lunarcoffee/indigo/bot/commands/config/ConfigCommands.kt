package dev.lunarcoffee.indigo.bot.commands.config

import dev.lunarcoffee.indigo.bot.util.prefixes.PrefixManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.bot.util.zones.ZoneManager
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRemaining
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrTimeZone

@CommandGroup("Config")
class ConfigCommands {
    fun setPrefix() = command("setprefix") {
        description = "Sets the accepted bot prefixes for this server, overriding the previous prefixes."

        execute(TrRemaining) { (prefixes) ->
            check(prefixes, "I can't have more than 10 prefixes!") { size > 10 }
            check(prefixes, "Prefixes must be 1 to 5 characters long (inclusive)!") { any { it.length !in 1..5 } }
            check(prefixes, "Prefixes may not contain spaces or backticks!") { any { ' ' in it || '`' in it } }

            if (checkFailed)
                return@execute

            PrefixManager.setPrefix(guild.id, prefixes.distinct())
            success("My prefixes for this server have been updated!")
        }
    }

    fun setTimezone() = command("settz", "settimezone") {
        description = "Sets your timezone (you can try a major city or any Java ZoneId) for the bot on all servers."

        execute(TrTimeZone(true)) { (zone) ->
            ZoneManager.setZone(event.author.id, zone)
            success("Your time zone has been updated!")
        }
    }
}
