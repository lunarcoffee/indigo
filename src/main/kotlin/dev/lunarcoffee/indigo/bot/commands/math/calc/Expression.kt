package dev.lunarcoffee.indigo.bot.commands.math.calc

sealed class Expression {
    sealed class Term : Expression() {
        class Number(val value: Double) : Term()
        class Variable(val name: String) : Term()

        sealed class UnaryOp(val operand: Expression) : Term() {
            class Minus(operand: Expression) : UnaryOp(operand)
            class Abs(operand: Expression) : UnaryOp(operand)
            class Factorial(operand: Expression) : UnaryOp(operand)
            class Function(val name: String, operand: Expression) : UnaryOp(operand)
        }
    }

    sealed class BinOp(val left: Expression, val right: Expression) : Expression() {
        class Plus(left: Expression, right: Expression) : BinOp(left, right)
        class Minus(left: Expression, right: Expression) : BinOp(left, right)
        class Multiply(left: Expression, right: Expression) : BinOp(left, right)
        class Divide(left: Expression, right: Expression) : BinOp(left, right)
        class Exponent(left: Expression, right: Expression) : BinOp(left, right)
    }
}
