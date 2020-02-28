package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.std.HasBot
import dev.lunarcoffee.indigo.framework.core.std.HasJDA
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.GenericEvent

interface CommandContext : HasBot, HasJDA, TextChannel {
    val event: GenericEvent
    val invokedPrefix: String
}
