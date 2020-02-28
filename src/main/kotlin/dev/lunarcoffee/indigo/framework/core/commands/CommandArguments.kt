package dev.lunarcoffee.indigo.framework.core.commands

interface CommandArguments {
    val size: Int

    fun asList(): List<*>
}

class Arg0 : CommandArguments {
    override val size = 0

    override fun asList() = emptyList<Any>()
}

data class Arg1<A>(val a: A) : CommandArguments {
    override val size = 1

    override fun asList() = listOf(a)
}

data class Arg2<A, B>(val a: A, val b: B) : CommandArguments {
    override val size = 2

    override fun asList() = listOf(a, b)
}

data class Arg3<A, B, C>(val a: A, val b: B, val c: C) : CommandArguments {
    override val size = 3

    override fun asList() = listOf(a, b, c)
}

data class Arg4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D) : CommandArguments {
    override val size = 4

    override fun asList() = listOf(a, b, c, d)
}

data class Arg5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E) : CommandArguments {
    override val size = 5

    override fun asList() = listOf(a, b, c, d, e)
}
