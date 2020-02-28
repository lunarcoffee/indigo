package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.builders.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = "pings bot"
        execute(TrInt()) { (a) -> send("pong $a") }
    }

    fun add() = command("add") {
        description = "adds some numbers"
        execute(TrInt(), TrInt()) { (a, b) -> send("$a + $b = ${a + b}") }
    }
}
