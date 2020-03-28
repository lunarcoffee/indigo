package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import net.dv8tion.jda.api.entities.TextChannel

object TrTextChannel : Transformer<TextChannel, GuildCommandContext> {
    private val channelMention = """<#(\d{18})>""".toRegex()
    private val channelId = """\d{18}""".toRegex()

    override val errorMessage = "A text channel name or ID was formatted incorrectly or doesn't exist!"

    override fun transform(ctx: GuildCommandContext, args: MutableList<String>): TextChannel? {
        val nameOrId = args.firstOrNull() ?: return null
        val mentionMatch = channelMention.matchEntire(nameOrId)

        return when {
            mentionMatch != null -> ctx.guild.getTextChannelById(mentionMatch.groupValues[1])
            nameOrId matches channelId -> ctx.guild.getTextChannelById(nameOrId)
            else -> ctx.guild.getTextChannelsByName(nameOrId, true).firstOrNull()
        }?.also { args.removeAt(0) }
    }
}
