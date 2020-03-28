package dev.lunarcoffee.indigo.bot.commands.service.lol

import com.merakianalytics.orianna.types.common.Region
import com.merakianalytics.orianna.types.core.staticdata.ReforgedRune
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.distance
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class LeagueRuneInfoSender(private val runeNames: List<String>) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        // Try to get the rune given [runeNames]. If that fails, take the first with distance less than four from any
        // of the [runeNames]. If that fails, error out.
        val rune = runeNames
            .map { ReforgedRune.named(it).withRegion(Region.NORTH_AMERICA).get() }
            .firstOrNull { it.shortDescription != null }
            ?: LeagueInfo.allRunes.firstOrNull { rune -> runeNames.any { it.distance(rune.name) < 4 } }
        ctx.checkNull(rune, "That rune does not exist!") ?: return

        val formatted = rune!!.longDescription.formatTags()
        ctx.send(
            embed {
                title = "${Emoji.BOOK}  Info on rune **${rune.name}**:"
                description = formatted[0]
                thumbnail = "$RUNE_IMAGE_ROOT/${rune.image.toLowerCase()}"

                field("Additional information", formatted.getOrNull(1) ?: return@embed)
            }
        )
    }

    private fun String.formatTags() = replace(LeagueInfo.lineBreakRegex, "\n")
        .replace(LeagueInfo.tagRegex, "")
        .split("\n", limit = 2)
        .map { p -> p.lines().joinToString("\n") { l -> l.replace(beforeColonRegex) { "**${it.groupValues[1]}**:" } } }

    companion object {
        private const val RUNE_IMAGE_ROOT =
            "http://raw.communitydragon.org/latest/plugins/rcp-be-lol-game-data/global/default/v1"

        private val beforeColonRegex = "^([^:]+):".toRegex()
    }
}
