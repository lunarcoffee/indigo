package dev.lunarcoffee.indigo.bot.math

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRestJoined

@CommandGroup("Math")
class MathCommands {
    fun calc() = command("calculate", "calc", "expr") {
        description = """
            |`$name <expression>`
            |Calculates the value of a mathematical expression.
            |
        """.trimMargin()

        execute(TrRestJoined) { (exprStr) ->
            // TODO: timeout execution, integrate/differentiate functions

        }
    }

    fun plot() = command("plot", "graph") {

    }
}
