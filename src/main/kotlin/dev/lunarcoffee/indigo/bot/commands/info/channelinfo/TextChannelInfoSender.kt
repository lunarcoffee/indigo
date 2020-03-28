package dev.lunarcoffee.indigo.bot.commands.info.channelinfo

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender
import dev.lunarcoffee.indigo.framework.core.std.TransformedTime
import net.dv8tion.jda.api.entities.TextChannel

class TextChannelInfoSender(private val channel: TextChannel) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val slowMode = channel.slowmode.toLong().takeIf { it > 0 }?.run { TransformedTime(this) }.ifNullNone()
        ctx.send(
            embed {
                title = "${Emoji.MAG}  Info on text channel **#${channel.name}**:"
                description = """
                    |**Channel ID**: ${channel.id}
                    |**Topic**: ${channel.topic.ifNullNone()}
                    |**Slowmode**: $slowMode
                    |**NSFW**: ${channel.isNSFW.toYesNo()}
                    |**Mention**: ${channel.asMention}
                    |**Time created**: ${channel.timeCreated.formatDefault()}
                    |**Category**: ${channel.parent?.name.ifNullNone()}
                    |**Position**: ${channel.position + 1}
                """.trimMargin()
            }
        )
    }
}
