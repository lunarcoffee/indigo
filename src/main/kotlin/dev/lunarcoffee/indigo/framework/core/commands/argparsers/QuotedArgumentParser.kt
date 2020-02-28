package dev.lunarcoffee.indigo.framework.core.commands.argparsers

class QuotedArgumentParser(private val raw: String) : CommandArgumentParser {
    private var pos = 0
    private var curChar = raw[pos]

    override fun split(): List<String> {
        val args = mutableListOf<String>()

        while (true) {
            when (curChar) {
                '\u0000' -> return args.drop(1)
                ' ' -> advance()
                '"', '\'' -> {
                    val char = curChar
                    advance()
                    args += readWhile { it != char && it != '\u0000' }
                }
                else -> args += readWhile { it != ' ' && it != '\u0000' }
            }
        }
    }

    private fun readWhile(predicate: (Char) -> Boolean): String {
        var string = ""
        while (predicate(curChar)) {
            string += curChar
            advance()
        }
        advance()
        return string
    }

    private fun advance() {
        pos++
        curChar = raw.getOrElse(pos) { '\u0000' }
    }
}
