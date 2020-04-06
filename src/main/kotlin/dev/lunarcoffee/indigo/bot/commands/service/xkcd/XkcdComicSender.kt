package dev.lunarcoffee.indigo.bot.commands.service.xkcd

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.sanitize
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class XkcdComicSender(private val number: Int?) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val comic = XkcdComicRequester(number).get()
        ctx.checkNull(comic, "I couldn't get that comic!") ?: return

        ctx.send(
            embed {
                title = "${Emoji.FRAMED_PICTURE}  Comic #${comic!!.number}:"
                description = """
                    |**Title**: ${comic.title.sanitize()}
                    |**Alt text**: ${comic.alt.sanitize()}
                    |**Release date**: ${comic.day.padStart(2, '0')}/${comic.month.padStart(2, '0')}/${comic.year}
                    |**Link**: [here](http://xkcd.com/${comic.number})
                """.trimMargin()
                image = comic.imageUrl
            }
        )
    }
}
