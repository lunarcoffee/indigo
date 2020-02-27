package dev.lunarcoffee.indigo.framework.core.commands

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

interface CommandExecutor {
    fun execute(command: Command, event: GuildMessageReceivedEvent)
}
