package dev.lunarcoffee.indigo.bot.commands.math.plot

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.sanitize
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class FunctionPlotSender(
    plotter: FunctionPlotter,
    private val funcs: List<String>,
    private val polar: Boolean,
    domain: Double
) : ContentSender {

    private val file = plotter.plot(polar, domain)

    override suspend fun send(ctx: CommandContext) {
        ctx.checkNull(file, "One or more of your functions was invalid!") ?: return

        ctx.sendMessage(
            embed {
                val titleEmoji = ifPolar(Emoji.CHART_DOWN, Emoji.CHART_UP)
                val titleGraphType = ifPolar("Polar", "Cartesian")
                val titleEnd = if (funcs.size == 1) "**${funcs[0].sanitize()}**" else "**${funcs.size}** functions"

                title = "$titleEmoji  $titleGraphType graph of $titleEnd:"
                image = "attachment://${file!!.name}"

                val variable = ifPolar("r", "y")
                description = funcs
                    .mapIndexed { index, function -> "**#${index + 1}**: $variable=${function.sanitize()}" }
                    .joinToString("\n")
            }
        ).addFile(file!!).await()
        file.delete()
    }

    private fun ifPolar(polarOption: String, cartesianOption: String) = if (polar) polarOption else cartesianOption
}
