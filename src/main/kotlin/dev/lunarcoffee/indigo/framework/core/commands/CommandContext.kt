package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.std.HasJDA
import net.dv8tion.jda.api.entities.TextChannel

interface CommandContext<T : CommandArguments> : HasJDA, TextChannel {
    val args: T
}
