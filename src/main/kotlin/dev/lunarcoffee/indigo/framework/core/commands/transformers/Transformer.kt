package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

interface Transformer<TResult, TContext : CommandContext> {
    val isOptional get() = false
    val errorMessage: String

    // If this returns [null], the transformation has failed.
    fun transform(ctx: TContext, args: MutableList<String>): TResult?

    // This returns an optional variant of this argument transformer.
    fun optional(): Transformer<TResult?, TContext> {
        return object : Transformer<TResult?, TContext> {
            override val isOptional = true
            override val errorMessage = this@Transformer.errorMessage

            override fun transform(ctx: TContext, args: MutableList<String>) = this@Transformer.transform(ctx, args)
        }
    }

    // This returns an optional variant of this argument transformer with a default taken from the [CommandContext].
    fun optional(default: TContext.() -> TResult): Transformer<TResult, TContext> {
        return object : Transformer<TResult, TContext> {
            override val isOptional = true
            override val errorMessage = this@Transformer.errorMessage

            override fun transform(ctx: TContext, args: MutableList<String>) = this@Transformer.transform(ctx, args)
                ?: default(ctx)
        }
    }
}
