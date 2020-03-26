package dev.lunarcoffee.indigo.bot.commands.math

import dev.lunarcoffee.indigo.bot.commands.math.calc.ExpressionCalculator
import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.command
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
            |value), and `sign` (sign function).
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
            check(value, "Your expression is invalid!") {
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
        execute(TrMany(TrWord)) { (exprStrs) ->
            send("This command is currently unavailable.")
        }
    }
}
