package dev.lunarcoffee.indigo.bot.commands.math.calc

class ExpressionParser(private val lexer: ExpressionLexer) {
    fun getTree(): Expression? {
        val tree = expression()
        return if (lexer.next() != Token.EOF) null else tree
    }

    private fun expression(minPrecedence: Int = 1): Expression? {
        var left = term() ?: return null
        var next = lexer.peek()

        while (next is Token.BinOp && next.precedence >= minPrecedence) {
            lexer.next()
            val right = expression(next.precedence + if (next.rightAssociative) 0 else 1) ?: return null

            left = when (next) {
                is Token.BinOp.Plus -> Expression.BinOp.Plus(left, right)
                is Token.BinOp.Minus -> Expression.BinOp.Minus(left, right)
                is Token.BinOp.Asterisk -> Expression.BinOp.Multiply(left, right)
                is Token.BinOp.Slash -> Expression.BinOp.Divide(left, right)
                is Token.BinOp.Caret -> Expression.BinOp.Exponent(left, right)
            }
            next = lexer.peek()
        }
        return left
    }

    private fun term(): Expression? {
        val term = when (val next = lexer.next()) {
            is Token.Number -> Expression.Term.Number(next.value)
            is Token.Identifier -> {
                // Just a name, not a function call.
                if (lexer.peek() != Token.LParen)
                    return Expression.Term.Variable(next.value)
                lexer.next()

                val expr = expression() ?: return null
                expectToken<Token.RParen>() ?: return null
                Expression.Term.UnaryOp.Function(next.value, expr)
            }
            Token.BinOp.Minus -> Expression.Term.UnaryOp.Minus(expression(4) ?: return null)
            Token.LParen -> expression().also { expectToken<Token.RParen>() ?: return null }
            Token.Pipe -> expression()
                .also { expectToken<Token.Pipe>() ?: return null }
                ?.run { Expression.Term.UnaryOp.Abs(this) }
            else -> null
        }

        // Factorial operator?
        return if (lexer.peek() != Token.Bang) {
            term
        } else {
            lexer.next()
            return Expression.Term.UnaryOp.Factorial(term ?: return null)
        }
    }

    private inline fun <reified T : Token> expectToken(): T? {
        val token = lexer.next()
        return if (token is T) token else null
    }
}
