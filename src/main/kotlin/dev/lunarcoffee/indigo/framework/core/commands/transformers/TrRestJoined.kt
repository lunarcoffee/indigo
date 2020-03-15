package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

object TrRestJoined : Transformer<String> {
    override val errorMessage = "I expected more information for this command!"

    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .joinToString(" ")
        .also { args.clear() }
        .ifEmpty { null }
}
