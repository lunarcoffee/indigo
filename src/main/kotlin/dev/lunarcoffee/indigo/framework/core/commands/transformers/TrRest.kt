package dev.lunarcoffee.indigo.framework.core.commands.transformers

object TrRest : Transformer<String> {
    override fun transform(args: MutableList<String>) = args.joinToString(" ").also { args.clear() }.ifEmpty { null }
}
