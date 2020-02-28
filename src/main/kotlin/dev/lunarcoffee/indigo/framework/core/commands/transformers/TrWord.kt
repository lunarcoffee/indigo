package dev.lunarcoffee.indigo.framework.core.commands.transformers

class TrWord : Transformer<String> {
    override fun transform(args: MutableList<String>) = args.firstOrNull()?.also { args.removeAt(0) }
}
