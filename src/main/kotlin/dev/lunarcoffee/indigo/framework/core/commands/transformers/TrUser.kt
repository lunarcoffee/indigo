package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.User

object TrUser : Transformer<User> {
    private val userTag = """[^@#:]{2,32}#\d{4}$""".toRegex()
    private val userMention = """<@!?(\d{18})>""".toRegex()
    private val userId = """\d{18}""".toRegex()

    override fun transform(ctx: CommandContext, args: MutableList<String>): User? {
        val nameOrId = args.firstOrNull() ?: return null
        args.removeAt(0)

        val mentionMatch = userMention.matchEntire(nameOrId)
        return when {
            nameOrId matches userTag -> ctx.jda.getUserByTag(nameOrId)
            nameOrId matches userId -> ctx.jda.getUserById(nameOrId)
            mentionMatch != null -> ctx.jda.getUserById(mentionMatch.groupValues[1])
            else -> ctx.jda.getUsersByName(nameOrId, true).firstOrNull()
        }
    }
}
