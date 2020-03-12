package dev.lunarcoffee.indigo.bot.commands.config

import dev.lunarcoffee.indigo.bot.util.prefixes.PrefixManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.bot.util.zones.ZoneManager
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRemaining
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrTimeZone
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrWord

@CommandGroup("Config")
class ConfigCommands {
    fun setCfgR() = command("setcfgr", "setconfigrole") {
        description = """
            
        """.trimMargin()

        execute(TrWord) { (role) ->
            // TODO: TrRole
        }
    }

    fun setPfx() = command("setpfx", "setprefix") {
        description = """
            |`$name <prefixes...>`
            |Sets the accepted bot prefixes for this server, overriding the previous prefixes.
            |This command allows you to change my prefixes (the symbols you put before a command name). There must
            |always be at least one prefix, but no more than ten. Each prefix can be a combination of symbols up to,
            |five characters in length, excluding the backtick (``${'\u200B'}`${'\u200B'}``) and space (` `). You must 
            |have the bot configurer role (see help for `setcfgr`) or the administrator permission to use this command.
            |&{Example usage:}
            |- `setpfx .. ::`\n
            |- `setpfx i!`\n
            |- `setpfx . ! $ &`
        """.trimMargin()

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

    fun setTz() = command("settz", "settimezone") {
        description = """
            |`$name <timezone city/name>`
            |Sets your timezone (you can try a major city or any Java ZoneId) for the bot on all servers.
            |This command allows you to use other commands which depend on your timezone, like `remindat`. The `timezone
            |city/name` should be the city corresponding to your timezone, or anything on 
            |[this](https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/) list will probably
            |work.
            |&{Example usage:}
            |- `settz toronto`\n
            |- `settz gmt+4`
        """.trimMargin()

        execute(TrTimeZone(true)) { (zone) ->
            ZoneManager.setZone(event.author.id, zone)
            success("Your time zone has been updated!")
        }
    }
}
