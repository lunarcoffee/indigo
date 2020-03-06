package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

object TrWord : Transformer<String> {
    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .firstOrNull()
        ?.also { args.removeAt(0) }
}
