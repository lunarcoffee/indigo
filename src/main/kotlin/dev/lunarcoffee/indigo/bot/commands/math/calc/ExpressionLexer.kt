package dev.lunarcoffee.indigo.bot.commands.math.calc

import java.util.*

class ExpressionLexer(private val exprStr: String) {
    private var pos = 0
    private var curChar = exprStr[pos]
    private val peekQueue: Queue<Token> = LinkedList()

    fun next(): Token? {
        if (peekQueue.isNotEmpty())
            return peekQueue.poll()

        if (curChar.isDigit())
            return Token.Number(readNumber())
        if (curChar.isLetter())
            return Token.Identifier(readWhile { it.isLetter() })

        return when (curChar) {
            ' ' -> {
                advance()
                next()
            }
            '+' -> Token.BinOp.Plus
            '-' -> Token.BinOp.Minus
            '*' -> Token.BinOp.Asterisk
            '/' -> Token.BinOp.Slash
            '^' -> Token.BinOp.Caret
            '|' -> Token.Pipe
            '!' -> Token.Bang
            '(' -> Token.LParen
            ')' -> Token.RParen
            '\u0000' -> Token.EOF
            else -> null
        }.also { advance() }
    }

    fun peek() = next().also { peekQueue.offer(it) }

    private fun readWhile(predicate: (Char) -> Boolean): String {
        var result = ""
        while (predicate(curChar)) {
            result += curChar
            advance()
        }
        return result
    }

    private fun readNumber(): Double {
        var dotFound = false
        return readWhile {
            if (it == '.' && !dotFound) {
                dotFound = true
                true
            } else {
                it.isDigit()
            }
        }.toDouble()
    }

    private fun advance() {
        pos++
        curChar = exprStr.getOrElse(pos) { '\u0000' }
    }
}
