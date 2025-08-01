package org.linkit

import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

sealed class Operation {
    sealed class Binary : Operation() {
        object Add : Binary()
        object Subtract : Binary()
        object Multiply : Binary()
        object Divide : Binary()
        object Power : Binary()
        object Modulo : Binary()
    }
    sealed class Unary : Operation() {
        object Negate : Unary()
        object Sin : Unary()
        object Cos : Unary()
        object Tan : Unary()
        object Sqrt : Unary()
        object Ln : Unary()
        object Log10 : Unary()
        object Abs : Unary()
    }
}

sealed class Expr {
    data class Number(val value: Double) : Expr()
    data class BinaryOp(val left: Expr, val op: Operation.Binary, val right: Expr) : Expr()
    data class UnaryOp(val op: Operation.Unary, val operand: Expr) : Expr()
    data class Variable(val name: String) : Expr()
}

class Calc {
    private val constants = mapOf("PI" to kotlin.math.PI, "E" to kotlin.math.E)

    fun eval(expr: Expr): Double =
            when (expr) {
                is Expr.Number -> expr.value
                is Expr.Variable -> constants[expr.name]
                                ?: throw IllegalArgumentException("Unknown variable: ${expr.name}")
                is Expr.BinaryOp -> {
                    val left = eval(expr.left)
                    val right = eval(expr.right)
                    evaluate_binary(left, expr.op, right)
                }
                is Expr.UnaryOp -> {
                    val operand = eval(expr.operand)
                    evaluate_unary(expr.op, operand)
                }
            }

    private fun evaluate_binary(left: Double, op: Operation.Binary, right: Double): Double =
            when (op) {
                Operation.Binary.Add -> left + right
                Operation.Binary.Subtract -> left - right
                Operation.Binary.Multiply -> left * right
                Operation.Binary.Divide -> {
                    if (right == 0.0) throw ArithmeticException("Division by zero")
                    left / right
                }
                Operation.Binary.Power -> left.pow(right)
                Operation.Binary.Modulo -> left % right
            }

    private fun evaluate_unary(op: Operation.Unary, operand: Double): Double =
            when (op) {
                Operation.Unary.Negate -> -operand
                Operation.Unary.Sin -> kotlin.math.sin(operand)
                Operation.Unary.Cos -> kotlin.math.cos(operand)
                Operation.Unary.Tan -> kotlin.math.tan(operand)
                Operation.Unary.Sqrt -> {
                    if (operand < 0) throw ArithmeticException("Square root of negative number")
                    kotlin.math.sqrt(operand)
                }
                Operation.Unary.Ln -> {
                    if (operand <= 0)
                            throw ArithmeticException("Natural log of non-positive number")
                    kotlin.math.ln(operand)
                }
                Operation.Unary.Log10 -> {
                    if (operand <= 0) throw ArithmeticException("Log10 of non-positive number")
                    kotlin.math.log10(operand)
                }
                Operation.Unary.Abs -> kotlin.math.abs(operand)
            }
}
