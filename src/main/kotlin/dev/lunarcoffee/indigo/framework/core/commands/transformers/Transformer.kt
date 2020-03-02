package dev.lunarcoffee.indigo.framework.core.commands.transformers

interface Transformer<T> {
    val isOptional get() = false

    // If this returns [null], the transformation has failed.
    fun transform(args: MutableList<String>): T?

    // This returns an optional variant of this argument transformer.
    fun optional(): Transformer<T?> {
        return object : Transformer<T?> {
            override val isOptional = true
            override fun transform(args: MutableList<String>) = this@Transformer.transform(args)
        }
    }
}
