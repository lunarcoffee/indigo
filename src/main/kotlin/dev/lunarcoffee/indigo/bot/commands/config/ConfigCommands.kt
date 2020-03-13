package dev.lunarcoffee.indigo.bot.commands.config

import dev.lunarcoffee.indigo.bot.util.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.bot.util.zones.ZoneManager
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRemaining
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRole
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrTimeZone
import net.dv8tion.jda.api.Permission

@CommandGroup("Config")
class ConfigCommands {
    fun setCfgR() = command("setcfgr", "setconfigrole") {
        description = """
            |`$name <role name/id/ping>`
            |Sets the bot configurer role, allowing those with it to use server-wide config commands.
            |This command sets the bot configurer role. Anyone with the role can use server-wide conifg commands. It is
            |not required to use personal config commands, like `settz` for setting a user's timezone. You must have
            |the administrator permission to use this command.
            |&{Example usage:}
            |- `setcfgr Moderators`\n
            |- `setcfgr @Admin`\n
            |- `setcfgr 510820375022731274`
        """.trimMargin()

        execute(TrRole) { (role) ->
            checkPermission("You must be an admin to use this command!", Permission.ADMINISTRATOR) ?: return@execute

            GuildSettingsManager.update(guild.id, newConfigRoleId = role.id)
            success("The configurer role has been updated!")
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
            checkAdminOrConfigRole()
            check(prefixes, "I can't have more than 10 prefixes!") { size > 10 }
            check(prefixes, "Prefixes must be 1 to 5 characters long (inclusive)!") { any { it.length !in 1..5 } }
            check(prefixes, "Prefixes may not contain spaces or backticks!") { any { ' ' in it || '`' in it } }

            if (checkFailed)
                return@execute

            GuildSettingsManager.update(guild.id, newPrefixes = prefixes.distinct())
            success("My prefixes for this server have been updated!")
        }
    }

    fun setTz() = command("settz", "settimezone") {
        description = """
            |`$name <timezone city/name>`
            |Sets your timezone (you can try a major city or any Java ZoneId) for the bot on all servers.
            |This command lets you use other commands which depend on your timezone, like `remindat`. The `timezone
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

    private suspend fun GuildCommandContext.checkAdminOrConfigRole(): Unit? {
        val configRole = GuildSettingsManager.get(guild.id).configRoleId ?: ""
        return check(event.member!!, "You must be an admin or have the configurer role to use this command!") {
            !hasPermission(Permission.ADMINISTRATOR) && configRole !in roles.map { it.id }
        }
    }
}
