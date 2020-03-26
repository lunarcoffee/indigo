package dev.lunarcoffee.indigo.bot.commands.math.calc

sealed class Token {
    class Number(val value: Double) : Token()
    class Identifier(val value: String) : Token()

    sealed class BinOp(val precedence: Int, val rightAssociative: Boolean) : Token() {
        object Plus : BinOp(1, false)
        object Minus : BinOp(1, false)
        object Asterisk : BinOp(2, false)
        object Slash : BinOp(2, false)
        object Caret : BinOp(3, true)
    }

    object Pipe : Token()
    object Bang : Token()

    object LParen : Token()
    object RParen : Token()

    object EOF : Token()
}
