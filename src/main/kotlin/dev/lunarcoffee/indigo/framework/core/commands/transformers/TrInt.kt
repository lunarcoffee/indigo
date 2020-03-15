package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

object TrInt : Transformer<Int> {
    override val errorMessage = "An integer was formatted incorrectly!"

    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .firstOrNull()
        ?.toIntOrNull()
        ?.also { args.removeAt(0) }
}
