package dev.lunarcoffee.indigo.bot.commands.math.calc

import kotlin.math.*

class ExpressionCalculator(private val exprStr: String) {
    fun calculate(): Double? {
        val expression = ExpressionParser(ExpressionLexer(exprStr)).getTree() ?: return null
        return runCatching { traverse(expression) }.getOrNull()
    }

    private fun traverse(expr: Expression): Double {
        return when (expr) {
            is Expression.Term.Number -> expr.value
            is Expression.Term.UnaryOp.Minus -> -traverse(expr.operand)
            is Expression.Term.UnaryOp.Abs -> traverse(expr.operand).absoluteValue
            is Expression.Term.UnaryOp.Factorial -> (2..traverse(expr.operand).roundToInt())
                .fold(1.0) { acc, i -> acc * i }
            is Expression.Term.Constant -> constant(expr)
            is Expression.Term.UnaryOp.Function -> function(expr)
            is Expression.BinOp.Plus -> traverse(expr.left) + traverse(expr.right)
            is Expression.BinOp.Minus -> traverse(expr.left) - traverse(expr.right)
            is Expression.BinOp.Multiply -> traverse(expr.left) * traverse(expr.right)
            is Expression.BinOp.Divide -> traverse(expr.left) / traverse(expr.right)
            is Expression.BinOp.Exponent -> traverse(expr.left).pow(traverse(expr.right))
        }
    }

    private fun constant(constant: Expression.Term.Constant): Double {
        return when (constant.name.toLowerCase()) {
            "pi" -> PI
            "e" -> E
            "phi" -> PHI
            "gamma" -> GAMMA
            else -> throw IllegalArgumentException()
        }
    }

    private fun function(func: Expression.Term.UnaryOp.Function): Double {
        val operand by lazy { traverse(func.operand) }
        return when (func.name) {
            "sin" -> sin(operand)
            "cos" -> cos(operand)
            "tan" -> tan(operand)
            "csc" -> 1 / sin(operand)
            "sec" -> 1 / cos(operand)
            "cot" -> 1 / tan(operand)
            "asin" -> asin(operand)
            "acos" -> acos(operand)
            "atan" -> atan(operand)
            "acsc" -> 1 / asin(operand)
            "asec" -> 1 / acos(operand)
            "acot" -> 1 / atan(operand)
            "sinh" -> sinh(operand)
            "cosh" -> cosh(operand)
            "tanh" -> tanh(operand)
            "asinh" -> asinh(operand)
            "acosh" -> acosh(operand)
            "atanh" -> atanh(operand)
            "sqrt" -> sqrt(operand)
            "ln" -> ln(operand)
            "log" -> log10(operand)
            "ceil" -> ceil(operand)
            "floor" -> floor(operand)
            "abs" -> abs(operand)
            "sign" -> sign(operand)
            else -> throw IllegalArgumentException()
        }
    }

    companion object {
        private const val PHI = 1.6180339887498948482
        private const val GAMMA = 0.5772156649015328606
    }
}
