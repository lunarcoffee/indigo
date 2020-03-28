package dev.lunarcoffee.indigo.bot.commands.info.channelinfo

import dev.lunarcoffee.indigo.bot.util.*
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender
import net.dv8tion.jda.api.entities.VoiceChannel

class VoiceChannelInfoSender(private val channel: VoiceChannel) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                title = "${Emoji.MAG}  Info on voice channel **#${channel.name}**:"
                description = """
                    |**Channel ID**: ${channel.id}
                    |**Bitrate**: ${channel.bitrate / 1_000} kb/s
                    |**User limit**: ${channel.userLimit.ifZeroNone()}
                    |**Time created**: ${channel.timeCreated.formatDefault()}
                    |**Category**: ${channel.parent?.name.ifNullNone()}
                    |**Position**: ${channel.position + 1}
                """.trimMargin()
            }
        )
    }
}
