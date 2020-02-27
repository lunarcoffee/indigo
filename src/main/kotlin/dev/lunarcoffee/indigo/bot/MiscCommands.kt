package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.core.commands.Command
import dev.lunarcoffee.indigo.framework.core.commands.CommandGroup
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommand

@CommandGroup("Misc")
class MiscCommands {
    fun ping(): Command = GuildCommand("ping", listOf("pong", "peng"), "pings bot") {
        sendMessage("pong!")
    }
}
