package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

interface Transformer<T> {
    val isOptional get() = false

    // If this returns [null], the transformation has failed.
    fun transform(ctx: CommandContext, args: MutableList<String>): T?

    // This returns an optional variant of this argument transformer.
    fun optional(default: T? = null): Transformer<T?> {
        return object : Transformer<T?> {
            override val isOptional = true

            override fun transform(ctx: CommandContext, args: MutableList<String>) =
                this@Transformer.transform(ctx, args) ?: default
        }
    }
}
