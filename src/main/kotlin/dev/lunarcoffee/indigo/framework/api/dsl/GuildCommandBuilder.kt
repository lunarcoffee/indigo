@file:Suppress("UNCHECKED_CAST")

package dev.lunarcoffee.indigo.framework.api.dsl

import dev.lunarcoffee.indigo.framework.core.commands.*
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

class GuildCommandDSL(override val name: String, override val aliases: List<String>) : Command {
    override var description = ""

    override lateinit var args: CommandArguments
        private set

    override lateinit var execute: CommandBody<CommandArguments>
        private set

    fun execute(body: GuildCommandBody<Arg0>) {
        args = Arg0
        execute = body as CommandBody<CommandArguments>
    }

    fun <A> execute(a: Transformer<A, out CommandContext>, body: GuildCommandBody<Arg1<A>>) {
        args = Arg1(a)
        execute = body as CommandBody<CommandArguments>
    }

    fun <A, B> execute(
        a: Transformer<A, out CommandContext>,
        b: Transformer<B, out CommandContext>,
        body: GuildCommandBody<Arg2<A, B>>
    ) {
        args = Arg2(a, b)
        execute = body as CommandBody<CommandArguments>
    }

    fun <A, B, C> execute(
        a: Transformer<A, out CommandContext>,
        b: Transformer<B, out CommandContext>,
        c: Transformer<C, out CommandContext>,
        body: GuildCommandBody<Arg3<A, B, C>>
    ) {
        args = Arg3(a, b, c)
        execute = body as CommandBody<CommandArguments>
    }

    fun <A, B, C, D> execute(
        a: Transformer<A, out CommandContext>,
        b: Transformer<B, out CommandContext>,
        c: Transformer<C, out CommandContext>,
        d: Transformer<D, out CommandContext>,
        body: GuildCommandBody<Arg4<A, B, C, D>>
    ) {
        args = Arg4(a, b, c, d)
        execute = body as CommandBody<CommandArguments>
    }

    fun <A, B, C, D, E> execute(
        a: Transformer<A, out CommandContext>,
        b: Transformer<B, out CommandContext>,
        c: Transformer<C, out CommandContext>,
        d: Transformer<D, out CommandContext>,
        e: Transformer<E, out CommandContext>,
        body: GuildCommandBody<Arg5<A, B, C, D, E>>
    ) {
        args = Arg5(a, b, c, d, e)
        execute = body as CommandBody<CommandArguments>
    }

    fun executeUncheckedArgs(vararg arg: Transformer<*, out CommandContext>, body: GuildCommandBody<ArgUnchecked>) {
        args = ArgUnchecked(arg.toList())
        execute = body as CommandBody<CommandArguments>
    }
}

fun command(name: String, vararg aliases: String, init: GuildCommandDSL.() -> Unit): Command =
    GuildCommandDSL(name, aliases.toList()).apply(init)
