package dev.lunarcoffee.indigo.framework.core.commands

typealias CommandBody<TCommandArgs> = suspend CommandContext.(TCommandArgs) -> Unit
typealias GuildCommandBody<TCommandArgs> = suspend GuildCommandContext.(TCommandArgs) -> Unit
