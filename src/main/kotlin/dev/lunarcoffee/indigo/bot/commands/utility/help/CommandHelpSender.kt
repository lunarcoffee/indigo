package dev.lunarcoffee.indigo.bot.commands.utility.help

import dev.lunarcoffee.indigo.bot.util.consts.Emoji
import dev.lunarcoffee.indigo.framework.api.dsl.embed
import dev.lunarcoffee.indigo.framework.api.exts.send
import dev.lunarcoffee.indigo.framework.core.commands.Command
import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.std.ContentSender

class CommandHelpSender(private val command: Command) : ContentSender {
    override suspend fun send(ctx: CommandContext) {
        val usage = command.description?.lines()?.take(1)?.get(0) ?: "(none)"
        val extendedDescription = command.description?.lines()?.drop(1)
        val shortDescription = extendedDescription?.take(1)?.get(0) ?: "(none)"

        ctx.send(
            embed {
                title = "${Emoji.PAGE_FACING_UP}  Info on command **${command.name}**:"
                description = """
                    |**Aliases**: ${command.names}
                    |**Description**: $shortDescription
                    |**Usage**: $usage
                """.trimMargin()

                // Turns the extended description into a list of pairs so that each pair represents a field. There is
                // always a default "Extended description:" field. Every field has its text associated with it, and a
                // new field is defined by the existence of a line looking like "&{Name of field:}". The next lines of
                // the description until the next field tag is met are incorporated into that tag's text.
                val extended = extendedDescription
                    ?.drop(1)
                    ?.fold(listOf(Pair("Extended description:", ""))) { acc, line ->
                        val match = FIELD_REGEX.matchEntire(line)
                        if (match != null)
                            acc + listOf(Pair(match.groupValues[1], ""))
                        else
                            acc.dropLast(1) + Pair(acc.last().first, "${acc.last().second}\n$line")
                    }
                    ?: return@embed

                for ((fieldName, fieldLines) in extended)
                    field(fieldName, fieldLines.replace('\n', ' ').replace("\\n", "\n"))
            }
        )
    }

    companion object {
        private val FIELD_REGEX = """&\{([^{}]+)}""".toRegex()
    }
}
