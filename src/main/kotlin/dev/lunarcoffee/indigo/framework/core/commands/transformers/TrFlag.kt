package dev.lunarcoffee.indigo.framework.core.commands.transformers

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext

class TrFlag<T>(
    private val flagName: String,
    private val ignoreCase: Boolean = false,
    private val conversionFunction: (String) -> T?
) : Transformer<T, CommandContext> {

    override val errorMessage = "A flag was formatted incorrectly!"

    override fun transform(ctx: CommandContext, args: MutableList<String>): T? {
        val first = args.firstOrNull() ?: return null

        val parts = first.split('=', limit = 2)
        if (parts.size < 2)
            return null

        val (name, stringValue) = parts
        if (!name.equals(flagName, ignoreCase))
            return null

        return conversionFunction(stringValue)?.also { args.removeAt(0) } ?: return null
    }
}
