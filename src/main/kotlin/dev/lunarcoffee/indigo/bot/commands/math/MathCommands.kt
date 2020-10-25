package dev.lunarcoffee.indigo.bot.commands.math

import dev.lunarcoffee.indigo.bot.commands.math.calc.ExpressionCalculator
import dev.lunarcoffee.indigo.bot.commands.math.plot.FunctionPlotSender
import dev.lunarcoffee.indigo.bot.commands.math.plot.FunctionPlotter
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*
import java.math.RoundingMode
import kotlin.math.PI

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
            |- `$name ln(|-3!/2|)`
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
            |`$name <functions of x...>`
            |Plots up to five functions of a single variable on a cartesian coordinate plane.
            |This command takes up to five functions of a single variable `x` and plots them on a coordinate plane.
            |Each of the `functions` should be an expression of a single variable `x` (can be thought of as the right
            |side of an equation `f(x)=...`). For information on what constitutes a valid expression, consult the help
            |text for the `calculate` command. The function will be evaluated with arguments in the interval [-10, 10].
            |&{Example usage:}
            |- `$name 3x^2 x^3`\n
            |- `$name e^x ln(x) sin(x) asin(x)`\n
            |- `$name x*floor(1/x)`
        """.trimMargin()

        execute(TrMany(TrWord)) { (funcs) ->
            check(funcs, "I will only plot up to five functions!") { size > 5 } ?: return@execute
            send(FunctionPlotSender(FunctionPlotter(funcs, bot), funcs, false, 0.0))
        }
    }

    fun plotPolar() = command("plotpolar", "graphpolar") {
        description = """
            |`$name ["domain"=number] <functions of t...>`
            |Plots up to five functions of a single variable on a polar coordinate plane.
            |This command takes up to five functions of a single variable `t` (for theta) and plots them on a polar
            |coordinate plane. Each of the `functions` should be an expression of a single variable `t` (can be thought 
            |of as the right side of an equation `r(t)=...`). For information on what constitutes a valid expression, 
            |consult the help text for the `calculate` command. The function will be evaluated with arguments in the
            |interval [0, 2pi], though you can set the `domain` flag to change the upper bound to a value in [pi/12, 
            |256pi] (see examples below).
            |&{Example usage:}
            |- `$name sin(t)`\n
            |- `$name 5*cos(3*t) 3*cos(5*t)`\n
            |- `$name domain=3.14 1/(1-cos(t))`
        """.trimMargin()

        execute(TrFlag("domain") { it.toDoubleOrNull() }.optional { 2 * PI }, TrMany(TrWord)) { (domain, funcs) ->
            check(funcs, "I will only plot up to five functions!") { size > 5 } ?: return@execute
            check(domain, "That upper bound is out of range!") { this !in PI / 12..256 * PI } ?: return@execute
            send(FunctionPlotSender(FunctionPlotter(funcs, bot), funcs, true, domain))
        }
    }
}
