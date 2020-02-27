package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

interface CommandExecutor : EventListener {
    var bot: CommandBot

    suspend fun execute(command: Command, event: GuildMessageReceivedEvent)
}
