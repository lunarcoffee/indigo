package dev.lunarcoffee.indigo.bot.commands.info

import dev.lunarcoffee.indigo.bot.commands.info.channelinfo.TextChannelInfoSender
import dev.lunarcoffee.indigo.bot.commands.info.channelinfo.VoiceChannelInfoSender
import dev.lunarcoffee.indigo.bot.commands.info.serverinfo.ServerInfoSender
import dev.lunarcoffee.indigo.bot.commands.info.userinfo.UserInfoSender
import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.VoiceChannel

@CommandGroup("Info")
class InfoCommands {
    fun userInfo() = command("userinfo", "memberinfo", "ui", "mi") {
        description = """
            |`$name [user name/id/ping]`
            |Shows detailed information about a given member of this server.
            |This command displays the nickname, ID, server join time, account creation time, current activity, status,
            |ping, server boost start time, color, roles, permissions, and avatar ID of the given user. It also shows
            |whether or not the account belongs to a bot and if it is boosting this server. If no user is specified, I
            |will show information about you.
            |&{Example usage:}
            |- `$name`\n
            |- `$name JohnB24`
        """.trimMargin()

        execute(TrMember.optional { event.member!! }) { (member) -> send(UserInfoSender(member)) }
    }

    fun serverInfo() = command("serverinfo", "si") {
        description = """
            |`$name`
            |Shows detailed information about this server.
            |This command displays the ID, member count, channel counts (total, text, and voice), category count, NSFW
            |filter message, creation time, region, features, boost tier, booster count, boost count, description,
            |owner, verification level, banner art and ID, vanity invite URL code, splash art and ID, MFA requirement, 
            |roles, and permissions given by the public role of this server.
        """.trimMargin()

        execute { send(ServerInfoSender(event.guild)) }
    }

    fun channelInfo() = command("channelinfo", "ci") {
        description = """
            |`$name [text or voice channel name/id/ping]`
            |Shows detailed information about a given text or voice channel of this server.
            |This command displays the ID, topic, slowmode, NSFW content status, mention, creation time, category, and
            |position of a text channel, or the ID, bitrate (in kb/s), user limit, creation time, category, and
            |position of a voice channel. If no channel is specified, the current channel is used.
            |&{Example usage:}
            |- `$name`\n
            |- `$name #general`\n
            |- `$name generalVoice`
        """.trimMargin()

        execute(TrGuildChannel.optional { event.channel }) { (channel) ->
            send(
                when (channel) {
                    is TextChannel -> TextChannelInfoSender(channel)
                    is VoiceChannel -> VoiceChannelInfoSender(channel)
                    else -> {
                        failureDefault(this@command.name)
                        return@execute
                    }
                }
            )
        }
    }

    fun roleInfo() = command("roleinfo", "ri") {
        description = """
            |`$name [role name/id/ping]`
            |Shows detailed information about a given role of this server.
            |This command displays the ID, ping, creation time, color, position, and permissions granted by the given
            |role. It also shows whether or not the role is displayed separately, pingable, and the number of members
            |with the role. If no role is provided, I will show information about the public role (called `@everyone`)
            |in the client.
            |&{Example usage:}
            |- `$name`\n
            |- `$name Member`
        """.trimMargin()

        execute(TrRole.optional { event.guild.publicRole }) { (role) ->
            send(
                embed {
                    val titleEnd = if (role.isPublicRole) "the **public role" else "role **${role.name.sanitize()}"
                    title = "${Emoji.MAG}  Info on $titleEnd**:"
                    description = """
                        |**Role ID**: ${role.id}
                        |**Displayed separately**: ${role.isHoisted.toYesNo()}
                        |**Mentionable**: ${role.isMentionable.toYesNo()}
                        |**Mention**: ${role.asMention}
                        |**Time created**: ${role.timeCreated.formatDefault()}
                        |**Managed**: ${role.isManaged}
                        |**Color**: #${role.colorRaw.toString(16).toUpperCase()}
                        |**Position**: ${role.position.takeIf { it >= 0 }?.plus(1).ifNullNone()}
                        |**Members with role**: ${event.guild.getMembersWithRoles(role).size}
                        |**Permissions**: ${role.permissions.map { "`$it`" }.ifEmptyNone()}
                    """.trimMargin()
                }
            )
        }
    }
}
