package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

object TrDouble : Transformer<Double> {
    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .firstOrNull()
        ?.toDoubleOrNull()
        ?.also { args.removeAt(1) }
}