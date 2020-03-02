package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRest

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = "pings bot"
        execute(TrInt.optional()) { (a) -> send("pong $a") }
    }

    fun add() = command("add") {
        description = "adds some numbers"
        execute(TrInt, TrInt, TrRest.optional()) { (a, b, c) -> send("$a + $b = ${a + b}\n$c") }
    }
}
