package dev.lunarcoffee.indigo.bot.commands.info

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.*
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrMember
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRole

@CommandGroup("Info")
class InfoCommands {
    fun channelInfo() = command("channelinfo", "ci") {
        description = """
            
        """.trimMargin()

        execute {
            // TODO: TrChannel
            send("This command is not available.")
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

        execute(TrRole.optional()) { (optionalRole) ->
            val role = optionalRole ?: event.guild.publicRole
            send(
                embed {
                    val titleEnd = optionalRole?.run { "role **${role.name.sanitize()}" } ?: "the **public role"
                    title = "${Emoji.MAG}  Info on $titleEnd**:"
                    description = """
                        |**Role ID**: ${role.id}
                        |**Displayed separately**: ${role.isHoisted.toYesNo()}
                        |**Mentionable**: ${role.isMentionable.toYesNo()}
                        |**Mention**: ${role.asMention}
                        |**Time created**: ${role.timeCreated}
                        |**Managed**: ${role.isManaged}
                        |**Color**: #${role.colorRaw.toString(16).toUpperCase()}
                        |**Position**: ${role.position.takeIf { it > 0 }.ifNullNone()}
                        |**Members with role**: ${event.guild.getMembersWithRoles(role).size}
                        |**Permissions**: ${role.permissions.map { "`$it`" }.ifEmptyNone()}
                    """.trimMargin()
                }
            )
        }
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

        execute {
            val guild = event.guild
            send(
                paginator {
                    embedPage {
                        title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                        description = """
                            |**Server ID**: ${guild.id}
                            |**Total members**: ${guild.memberCount}
                            |**Total channels**: ${guild.channels.size}
                            |**Text channels**: ${guild.textChannels.size}
                            |**Voice channels**: ${guild.voiceChannels.size}
                            |**Total categories**: ${guild.categories.size}
                            |**NSFW filter**: ${guild.explicitContentLevel.description}
                            |**Region**: ${guild.region.getName()}
                            |**Time created**: ${guild.timeCreated.formatDefault()}
                            |**Features**: ${guild.features.map { "`$it`" }.ifEmptyNone()}
                        """.trimMargin()
                        thumbnail = guild.iconUrl
                    }
                    embedPage {
                        title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                        description = """
                            |**Boost tier**: ${guild.boostTier.key.takeIf { it > 0 }.ifNullNone()}
                            |**Boosters**: ${guild.boosters.size}
                            |**Boost count**: ${guild.boostCount}
                            |**Description**: ${guild.description.ifNullNone()}
                            |**Owner**: ${guild.owner?.asMention.ifNullNone()}
                            |**Verification**: ${guild.verificationLevel.name.toLowerCase().replace('_', ' ')}
                            |**Banner art**: ${guild.bannerUrl.ifNullNoneElseLink()}
                            |**Banner ID**: ${guild.bannerId.ifNullNone()}
                            |**Vanity code**: ${guild.vanityCode.ifNullNone()}
                            |**Splash art**: ${guild.splashUrl.ifNullNoneElseLink()}
                        """.trimMargin()
                        thumbnail = guild.iconUrl
                    }
                    embedPage {
                        title = "${Emoji.MAG}  Info on server **${guild.name.sanitize()}**:"
                        description = """
                            |**Splash ID**: ${guild.splashId.ifNullNone()}
                            |**MFA required**: ${(guild.requiredMFALevel.key == 1).toYesNo()}
                            |**Roles**: ${guild.roles.map { it.asMention }.ifEmptyNone()}
                            |**Public permissions**: ${guild.publicRole.permissions.map { "`${it}`" }.ifEmptyNone()}
                        """.trimMargin()
                        thumbnail = guild.iconUrl
                    }
                }
            )
        }
    }

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

        execute(TrMember.optional()) { (optionalMember) ->
            val member = optionalMember ?: event.member!!
            send(
                paginator {
                    embedPage {
                        title = "${Emoji.MAG}  Info on member **${member.user.asTag.sanitize()}**:"
                        description = """
                            |**Nickname**: ${member.nickname.ifNullNone()}
                            |**User ID**: ${member.id}
                            |**Bot account**: ${member.user.isBot.toYesNo()}
                            |**Time joined**: ${member.timeJoined.formatDefault()}
                            |**Time account created**: ${member.timeCreated.formatDefault()}
                            |**Activity**: ${member.activities.firstOrNull()?.name.ifNullNone()}
                            |**Status**: ${member.onlineStatus.key}
                            |**Mention**: ${member.asMention}
                            |**Boosting server**: ${(member.timeBoosted != null).toYesNo()}
                            |**Time boosted**: ${member.timeBoosted.ifNullNone()}
                        """.trimMargin()
                        thumbnail = member.user.avatarUrl
                    }
                    embedPage {
                        title = "${Emoji.MAG}  Info on member **${member.user.asTag.sanitize()}**:"
                        description = """
                            |**Color**: #${member.colorRaw.toString(16).toUpperCase()}
                            |**Roles**: ${member.roles.map { it.asMention }.ifEmptyNone()}
                            |**Permissions**: ${member.permissions.map { "`$it`" }.ifEmptyNone()}
                            |**Avatar ID**: ${member.user.avatarId}
                        """.trimMargin()
                        thumbnail = member.user.avatarUrl
                    }
                }
            )
        }
    }
}
