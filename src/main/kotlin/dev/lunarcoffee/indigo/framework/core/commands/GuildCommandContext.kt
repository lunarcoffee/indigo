package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.bot.Bot
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class GuildCommandContext(
    override val bot: Bot,
    override val invokedPrefix: String,
    override val event: GuildMessageReceivedEvent
) : CommandContext, TextChannel by event.channel {

    // Calls to check methods will modify this if they have failed.
    override var checkFailed = false

    override val jda = bot.jda

    suspend fun checkPermission(failureMessage: String, permission: Permission) =
        check(event.member!!, failureMessage) { !hasPermission(permission) }
}
