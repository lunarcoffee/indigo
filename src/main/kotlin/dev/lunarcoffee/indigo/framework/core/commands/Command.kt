package dev.lunarcoffee.indigo.framework.core.commands

import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

interface Command {
    val name: String
    val aliases: List<String>
    val names get() = aliases + name

    val description: String?

    val args: CommandArguments
    val argTransfomers
        get() = args.asList().map {
            @Suppress("UNCHECKED_CAST")
            it as Transformer<*, CommandContext>
        }

    val execute: CommandBody<CommandArguments>
}
