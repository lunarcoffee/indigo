package dev.lunarcoffee.indigo.framework.core.commands.transformers

object TrInt : Transformer<Int> {
    override fun transform(args: MutableList<String>) = args.firstOrNull()?.toIntOrNull()?.also { args.removeAt(0) }
}
