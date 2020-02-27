package dev.lunarcoffee.indigo.bot

import dev.lunarcoffee.indigo.framework.api.GuildCommandDSL
import dev.lunarcoffee.indigo.framework.core.commands.GuildCommandExecutor
import dev.lunarcoffee.indigo.framework.core.commands.argparsers.QuotedArgumentParser
import dev.lunarcoffee.indigo.framework.core.commands.transformers.TrInt

fun main() {
    GuildCommandDSL().apply {
        execute(TrInt(), TrInt()) {
            val (a, b) = args
            sendMessage("$a + $b = ${a + b}").queue()
        }
    }

    val text = """a a sd"pok sdfoji'f' ejwie" a " a bc de" fg "abcef" "abc def"bc 'betsys "lol" cake' 'test""""
    val p = QuotedArgumentParser(text).split()
    println(p.joinToString("\n"))
}
