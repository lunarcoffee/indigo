package dev.lunarcoffee.indigo.framework.core.commands

interface CommandArguments

class Arg0 : CommandArguments
data class Arg1<A>(val a: A) : CommandArguments
data class Arg2<A, B>(val a: A, val b: B) : CommandArguments
data class Arg3<A, B, C>(val a: A, val b: B, val c: C) : CommandArguments
data class Arg4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D) : CommandArguments
data class Arg5<A, B, C, D, E>(val a: A, val b: B, val c: C, val d: D, val e: E) : CommandArguments
