package dev.lunarcoffee.indigo.bot.commands.math

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.*

@CommandGroup("Math")
class MathCommands {
    fun calc() = command("calculate", "calc", "expr") {
        description = """
            |`$name <expression>`
            |Calculates the value of a mathematical expression.
            |
        """.trimMargin()

        execute(TrRestJoined) { (exprStr) ->
            // TODO: timeout execution
            send("This command is currently unavailable.")
        }
    }

    fun plot() = command("plot", "graph") {
        execute(TrMany(TrWord)) { (exprStrs) ->
            send("This command is currently unavailable.")
        }
    }
}
