package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

object TrRemaining : Transformer<List<String>> {
    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .also { args.clear() }
        .ifEmpty { null }
}
