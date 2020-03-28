package dev.lunarcoffee.indigo.bot.commands.info.userinfo

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.Member

class UserInfoSender(private val member: Member) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            ctx.paginator {
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
