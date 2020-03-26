package dev.lunarcoffee.indigo.bot.commands.math

import dev.lunarcoffee.indigo.bot.commands.math.calc.ExpressionCalculator
import dev.lunarcoffee.indigo.bot.commands.math.plot.FunctionPlotter
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.bot.util.sanitize
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.await
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import java.math.RoundingMode

@CommandGroup("Math")
class MathCommands {
    fun calc() = command("calculate", "calc", "expr") {
        description = """
            |`$name <expression>`
            |Calculates the value of a mathematical expression.
            |This command will evaluate the given mathematical `expression`. The supported operators include `+`, `-`,
            |`*`, `/`, `^` (exponentiation), `n!` (factorial), and `|n|` (absolute value). There are four defined
            |constants, `pi`, `e`, `phi`, and `gamma`. There are also many functions available, including the three
            |primary and three reciprocal trigonometric functions and their inverses, the three primary hyperbolic 
            |trigonometric functions and their inverses, `sqrt`, `ln` (natural logarithm), `log` (base 10 logarithm),
            |`ceil` (ceiling/least integer function), `floor` (floor/greatest integer function), `abs` (absolute
            |value), `exp`, `gamma`, and `sign` (sign function). Trigonometric functions take arguments in radians. The
            |factorial operator is implemented with an approximation of the gamma function.
            |&{Precision:}
            |The values calculated by this command are not guaranteed to be exact. Large numbers will be imprecise in
            |particular, and very large numbers may yield an error message. All values are rounded to five decimal
            |places before being shown.
            |&{Example usage:}
            |- `$name 2+2`\n
            |- `$name 3*(2+sin(pi))+e^2`\n
            |- `$name ln(|-3/2|)`
        """.trimMargin()

        execute(TrRestJoined) { (exprStr) ->
            val value = ExpressionCalculator(exprStr).calculate()

            checkNull(value, "Your expression is invalid!") ?: return@execute
            check(value, "The result was too large, small, or undefined!") {
                this!!.isNaN() || this == Double.POSITIVE_INFINITY || this == Double.NEGATIVE_INFINITY
            } ?: return@execute

            val rounded = value!!
                .toBigDecimal()
                .setScale(5, RoundingMode.HALF_EVEN)
                .toString()
                .trimEnd('0')
                .trim('.')
            send("${Emoji.ABACUS}  **The result is `$rounded`.**")
        }
    }

    fun plot() = command("plot", "graph") {
        description = """
            |`$name <functions...>`
            |Plots up to five functions of a single variable.
            |This command takes up to five functions of a single variable `x` and plots them on a coordinate plane.
            |Each of the `functions` should be an expression of a single variable `x` (can be thought of as the right
            |side of an equation `f(x)=...`). For information on what constitutes a valid expression, consult the help
            |text for the `calculate` command.
            |&{Example usage:}
            |- `$name 3x^2 x^3`\n
            |- `$name e^x ln(x) sin(x) asin(x)`\n
            |- `$name x*floor(1/x)`
        """.trimMargin()

        execute(TrMany(TrWord)) { (funcs) ->
            val file = FunctionPlotter(funcs, bot).plot()
            checkNull(file, "One or more of your functions was invalid!") ?: return@execute

            sendMessage(
                embed {
                    val titleEnd = if (funcs.size == 1) "**${funcs[0].sanitize()}**" else "**${funcs.size}** functions"
                    title = "${Emoji.CHART_UPWARDS_TREND}  Graph of $titleEnd:"
                    description = funcs
                        .mapIndexed { index, function -> "**#${index + 1}**: y=${function.sanitize()}" }
                        .joinToString("\n")
                    image = "attachment://${file!!.name}"
                }
            ).addFile(file!!).await()
            file.delete()
        }
    }
}
