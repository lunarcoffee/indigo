package dev.lunarcoffee.indigo.framework.core.commands

typealias CommandBody<T> = suspend CommandContext.(T) -> Unit
typealias GuildCommandBody<T> = suspend GuildCommandContext.(T) -> Unit
