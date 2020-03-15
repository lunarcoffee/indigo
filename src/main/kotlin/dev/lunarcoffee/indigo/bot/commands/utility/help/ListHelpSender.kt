package dev.lunarcoffee.indigo.bot.commands.utility.help

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.bot.CommandBot
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class ListHelpSender(private val bot: CommandBot, private val isOwner: Boolean) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        ctx.send(
            embed {
                title = "${Emoji.PAGE_FACING_UP}  All commands:"
                description = bot
                    .commandGroups
                    .filter { (group, _) -> isOwner || group != "Restricted" }
                    .map { (group, commands) -> "**$group**: ${commands.map { it.name }}" }
                    .joinToString("\n")

                val prefix = ctx.invokedPrefix
                footer = "Try `${prefix}help help` or `${prefix}help <command name>`."
            }
        )
    }
}
