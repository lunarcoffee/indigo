package dev.lunarcoffee.indigo.bot.commands.misc

import dev.lunarcoffee.indigo.framework.api.dsl.command
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt

@CommandGroup("Misc")
class MiscCommands {
    fun ping() = command("ping", "pong", "peng") {
        description = "pings bot"
        execute(TrInt.optional(1)) { (a) -> send("pong $a") }
    }
}
