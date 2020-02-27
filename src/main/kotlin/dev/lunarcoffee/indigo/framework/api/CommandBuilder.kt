package dev.lunarcoffee.indigo.framework.api

import dev.lunarcoffee.indigo.framework.core.commands.*
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

class GuildCommandDSL(override val name: String) : Command {
    override var aliases = emptyList<String>()
    override var description = "(no description)"

    override lateinit var args: CommandArguments
    override lateinit var execute: CommandBody<out CommandArguments>

    fun <A> execute(a: Transformer<A>, body: CommandBody<Arg1<A>>) {
        args = Arg1(a)
        execute = body
    }

    fun <A, B> execute(a: Transformer<A>, b: Transformer<B>, body: CommandBody<Arg2<A, B>>) {
        args = Arg2(a, b)
    }
}

fun command(name: String, init: GuildCommandDSL.() -> Unit) = GuildCommandDSL(name).apply(init)
