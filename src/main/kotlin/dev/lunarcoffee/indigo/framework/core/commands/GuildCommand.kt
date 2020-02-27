package dev.lunarcoffee.indigo.framework.core.commands

class GuildCommand(
    override val name: String,
    override val aliases: List<String>,
    override val description: String?,
    override val args: CommandArguments,
    override val execute: suspend CommandContext<out CommandArguments>.() -> Unit
) : Command
