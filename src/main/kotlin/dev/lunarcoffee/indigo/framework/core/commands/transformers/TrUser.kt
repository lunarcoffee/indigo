package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.Member

object TrMember : Transformer<Member> {
    private val userTag = """[^@#:]{2,32}#\d{4}$""".toRegex()
    private val userMention = """<@!?(\d{18})>""".toRegex()
    private val userId = """\d{18}""".toRegex()

    override val errorMessage = "A user ping, name, or ID was formatted incorrectly or doesn't exist!"

    override fun transform(ctx: CommandContext, args: MutableList<String>): Member? {
        val nameOrId = args.firstOrNull() ?: return null
        args.removeAt(0)

        val mentionMatch = userMention.matchEntire(nameOrId)
        return when {
            mentionMatch != null -> ctx.guild.getMemberById(mentionMatch.groupValues[1])
            nameOrId matches userTag -> ctx.guild.getMemberByTag(nameOrId)
            nameOrId matches userId -> ctx.guild.getMemberById(nameOrId)
            else -> ctx.guild.getMembersByName(nameOrId, true).firstOrNull()
        }
    }
}
