package dev.lunarcoffee.indigo.framework.api.exts

import dev.lunarcoffee.indigo.framework.core.commands.CommandContext
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandContext
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

@Suppress("UNCHECKED_CAST")
fun <T, C : CommandContext> Transformer<T, C>.guild() = this as Transformer<T, GuildCommandContext>
