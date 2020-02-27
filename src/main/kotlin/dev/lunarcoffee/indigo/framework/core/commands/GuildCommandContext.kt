package dev.lunarcoffee.indigo.framework.core.commands

import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class GuildCommandContext<T : CommandArguments>(
    override val args: T,
    private val event: GuildMessageReceivedEvent
) : CommandContext<T>, TextChannel by event.channel {

    override val jda = event.jda
}
