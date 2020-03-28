package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

// It might be better to only use this with transformers which are not greedy in consuming arguments. This should
// definitely not be used with an optional transformer.
class TrMany<T>(val transformer: Transformer<T, CommandContext>) : Transformer<List<T>, CommandContext> {
    override val errorMessage = transformer.errorMessage

    init {
        if (transformer.isOptional)
            throw IllegalArgumentException()
    }

    override fun transform(ctx: CommandContext, args: MutableList<String>): List<T>? {
        if (args.isEmpty())
            return null

        val results = mutableListOf<T>()
        var result = transformer.transform(ctx, args)
        while (result != null) {
            results += result
            result = transformer.transform(ctx, args)
        }

        return results.ifEmpty { null }
    }
}
