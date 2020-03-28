package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import net.dv8tion.jda.api.entities.Member

object TrMember : Transformer<Member, GuildCommandContext> {
    private val userTag = """[^@#:]{2,32}#\d{4}$""".toRegex()
    private val userMention = """<@!?(\d{18})>""".toRegex()
    private val userId = """\d{18}""".toRegex()

    override val errorMessage = "A user ping, name, or ID was formatted incorrectly or doesn't exist!"

    override fun transform(ctx: GuildCommandContext, args: MutableList<String>): Member? {
        val nameOrId = args.firstOrNull() ?: return null
        val mentionMatch = userMention.matchEntire(nameOrId)

        return when {
            mentionMatch != null -> ctx.guild.getMemberById(mentionMatch.groupValues[1])
            nameOrId matches userTag -> ctx.guild.getMemberByTag(nameOrId)
            nameOrId matches userId -> ctx.guild.getMemberById(nameOrId)
            else -> ctx.guild.getMembersByName(nameOrId, true).firstOrNull()
        }?.also { args.removeAt(0) }
    }
}
