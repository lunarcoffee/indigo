package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrRestJoined
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrUser

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = "pings bot"
        execute(TrInt.optional(1)) { (a) -> send("pong $a") }
    }

    fun add() = command("add") {
        description = "adds some numbers"
        execute(TrInt, TrInt, TrRestJoined.optional()) { (a, b, c) -> send("$a + $b = ${a + b}\n$c") }
    }

    fun check() = command("check", "checkuser") {
        execute(TrUser) { (user) -> send("user with name: ${user.name}") }
    }
}
