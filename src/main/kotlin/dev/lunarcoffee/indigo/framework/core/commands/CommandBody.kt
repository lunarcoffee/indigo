package dev.lunarcoffee.indigo.framework.core.commands

typealias CommandBody<T> = suspend CommandContext.(T) -> Unit
