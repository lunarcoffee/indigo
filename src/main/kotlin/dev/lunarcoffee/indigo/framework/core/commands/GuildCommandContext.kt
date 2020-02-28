package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.Bot
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class GuildCommandContext(
    override val bot: Bot,
    event: GuildMessageReceivedEvent
) : CommandContext, TextChannel by event.channel {

    override val jda = bot.jda
}
