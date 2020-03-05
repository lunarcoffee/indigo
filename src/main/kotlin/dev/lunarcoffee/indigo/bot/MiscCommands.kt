package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.dsl.paginator
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
        execute(TrUser) { (user) ->
            send(
                paginator {
                    embedPage {
                        title = "user check:"
                        description = "name is ${user.name}"
                        color = 0xFFFF00
                    }
                    repeat(100) {
                        embedPage {
                            title = "user check:"
                            description = "page #${it + 2}"
                        }
                        textPage("helo")
                    }
                }
            )
        }
    }
}
