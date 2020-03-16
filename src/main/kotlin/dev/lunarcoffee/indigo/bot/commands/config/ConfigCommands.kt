package dev.lunarcoffee.indigo.bot.commands.config

import dev.lunarcoffee.indigo.bot.util.failure
import dev.lunarcoffee.indigo.bot.util.settings.guildsettings.GuildSettingsManager
import dev.lunarcoffee.indigo.bot.util.settings.usersettings.UserSettingsManager
import dev.lunarcoffee.indigo.bot.util.success
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import net.dv8tion.jda.api.Permission

@CommandGroup("Config")
class ConfigCommands {
    fun setConfigRole() = command("setconfigrole", "setcfgr") {
        description = """
            |`$name <role name/id/ping>`
            |Sets the bot configurer role, allowing those with it to use server-wide config commands.
            |This command sets the bot configurer role. Anyone with the role can use server-wide conifg commands. It is
            |not required to use personal config commands, like `settz` for setting a user's timezone. You must have
            |the administrator permission to use this command.
            |&{Example usage:}
            |- `$name Moderators`\n
            |- `$name @Admin`\n
            |- `$name 510820375022731274`
        """.trimMargin()

        execute(TrRole) { (role) ->
            checkPermission("You must be an admin to use this command!", Permission.ADMINISTRATOR) ?: return@execute

            GuildSettingsManager.update(guild.id, newConfigRoleId = role.id)
            success("The configurer role has been updated!")
        }
    }

    fun setPrefix() = command("setprefix", "setpfx") {
        description = """
            |`$name <prefixes...>`
            |Sets the accepted bot prefixes for this server, overriding the previous prefixes.
            |This command allows you to change my prefixes (the symbols you put before a command name). There must
            |always be at least one prefix, but no more than ten. Each prefix can be a combination of symbols up to,
            |five characters in length, excluding the backtick (``${'\u200B'}`${'\u200B'}``) and space (` `). You must 
            |have the bot configurer role (see help for `setcfgr`) or the administrator permission to use this command.
            |&{Example usage:}
            |- `$name .. ::`\n
            |- `$name i!`\n
            |- `$name . ! $ &`
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

    fun setTimezone() = command("settimezone", "settz") {
        description = """
            |`$name <timezone city/name>`
            |Sets your timezone (you can try a major city or any Java ZoneId) for the bot on all servers.
            |This command lets you use other commands which depend on your timezone, like `remindat`. The `timezone
            |city/name` should be the city corresponding to your timezone, or anything on 
            |[this](https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/) list will probably
            |work.
            |&{Example usage:}
            |- `$name toronto`\n
            |- `$name gmt+4`
        """.trimMargin()

        execute(TrTimeZone(true)) { (zone) ->
            UserSettingsManager.update(event.author.id, newZone = zone)
            success("Your time zone has been updated!")
        }
    }

    fun setStarboard() = command("setstarboard", "setsb") {
        description = """
            |`$name <"channel"|"threshold"|"off"> [channel name|star threshold]`
            |Configures the starboard for this server.
            |This command lets you set the starboard channel, threshold (minimum star count), and availability. If the
            |first argument is `channel`, the second must be a channel in the server. This will automatically enable
            |the starboard. If the first argument is `threshold`, the second must be the minimum star count. If the 
            |first argument is `on`, there should be no second argument. You must have the bot configurer role (see 
            |help for `setcfgr`) or the administrator permission to use this command.
            |&{Default values:}
            |By default, in order to be as non-intrusive as possible, the starboard is disabled. When it is enabled,
            |the star threshold will be just one. Disabling the starboard will not reset any previous settings.
            |&{Example usage:}
            |- `$name channel #starboard`\n
            |- `$name threshold 3`\n
            |- `$name off`
        """.trimMargin()

        execute(TrWord, TrTextChannel.optional(), TrInt.optional()) { (action, channel, threshold) ->
            checkAdminOrConfigRole() ?: return@execute

            val starboard = GuildSettingsManager.get(guild.id).starboard
            when (action) {
                "channel" -> {
                    checkNull(channel, "You must provide a channel to enable the starboard!") ?: return@execute
                    checkNotNull(threshold, "You must only provide a channel!") ?: return@execute

                    val newStarboard = starboard.apply {
                        channelId = channel!!.id
                        enabled = true
                    }
                    GuildSettingsManager.update(guild.id, newStarboard = newStarboard)
                    success("The starboard has been enabled!")
                }
                "threshold" -> {
                    checkNull(threshold, "You must provide a threshold for me to set!") ?: return@execute
                    checkNotNull(channel, "You must only provide a threshold!") ?: return@execute

                    val newStarboard = starboard.apply { this@apply.threshold = threshold!! }
                    GuildSettingsManager.update(guild.id, newStarboard = newStarboard)
                    success("The starboard threshold has been updated!")
                }
                "off" -> {
                    if (channel != null || threshold != null) {
                        failure("This action takes no arguments!")
                        return@execute
                    }

                    val newStarboard = starboard.apply { enabled = false }
                    GuildSettingsManager.update(guild.id, newStarboard = newStarboard)
                    success("The starboard has been disabled!")
                }
                else -> failure("That's not right. Type `${invokedPrefix}help ${this@command.name}` for information.")
            }
        }
    }

    private suspend fun GuildCommandContext.checkAdminOrConfigRole(): Unit? {
        val configRole = GuildSettingsManager.get(guild.id).configRoleId ?: ""
        return check(event.member!!, "You must be an admin or have the configurer role to use this command!") {
            !hasPermission(Permission.ADMINISTRATOR) && configRole !in roles.map { it.id }
        }
    }
}
