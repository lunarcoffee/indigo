package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

class TrRestSplit(private val delimiter: String) : Transformer<List<String>> {
    override fun transform(ctx: CommandContext, args: MutableList<String>) = args
        .joinToString(" ")
        .split(delimiter)
        .also { args.clear() }
        .ifEmpty { null }
}
