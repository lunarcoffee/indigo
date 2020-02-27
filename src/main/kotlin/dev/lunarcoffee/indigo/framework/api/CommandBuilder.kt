package dev.lunarcoffee.indigo.framework.api

import dev.lunarcoffee.indigo.framework.core.commands.Arg1
import dev.lunarcoffee.indigo.framework.core.commands.Arg2
import dev.lunarcoffee.indigo.framework.core.commands.CommandBody
import dev.lunarcoffee.indigo.framework.core.commands.transformers.Transformer

class GuildCommandDSL {
    fun <A> execute(a: Transformer<A>, body: CommandBody<Arg1<A>>) {

    }

    fun <A, B> execute(a: Transformer<A>, b: Transformer<B>, body: CommandBody<Arg2<A, B>>) {

    }
}
