package dev.lunarcoffee.indigo.framework.core.commands.transformers

interface Transformer<T> {
    // If this returns [null], the transformation has failed.
    fun transform(args: MutableList<String>): T?
}
