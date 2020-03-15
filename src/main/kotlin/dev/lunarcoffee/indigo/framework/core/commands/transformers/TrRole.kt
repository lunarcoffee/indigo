package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import net.dv8tion.jda.api.entities.Role

object TrRole : Transformer<Role> {
    private val roleMention = """<@&(\d{18})>""".toRegex()
    private val roleId = """\d{18}""".toRegex()

    override val errorMessage = "A role ping, name, or ID was formatted incorrectly or doesn't exist!"

    override fun transform(ctx: CommandContext, args: MutableList<String>): Role? {
        val nameOrId = args.firstOrNull() ?: return null
        args.removeAt(0)

        val mentionMatch = roleMention.matchEntire(nameOrId)
        return when {
            mentionMatch != null -> ctx.guild.getRoleById(mentionMatch.groupValues[1])
            nameOrId matches roleId -> ctx.guild.getRoleById(nameOrId)
            else -> ctx.jda.getRolesByName(nameOrId, true).firstOrNull { it.guild.id == ctx.guild.id }
        }
    }
}
