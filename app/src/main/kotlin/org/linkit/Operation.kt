package org.linkit

import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

class Degree{
    var value: Double = 0.0
    constructor(value: Double) {
        this.value = value
    }
    constructor(value: Int) {
        this.value = value.toDouble()
    }
    public fun to_radian(): Radian {
        if (value < 0 || value > 360) throw IllegalArgumentException("Degree value must be between 0 and 360")
        return Radian((value * kotlin.math.PI / 180).toInt(), 1)
    }
}

//// Represents an angle in radians, with numerator and denominator for precision
// This class allows for precise representation of angles in radians, useful for trigonometric calculations
// It supports operations like conversion to degrees and evaluation of trigonometric functions
// The value is calculated as (numerator/denominator) * π, ensuring high precision
// Usage: Radian(numerator, denominator)
class Radian{
    var coef_numer: Int = 0
    var coef_denom: Int = 1
    public fun value(): Double {
        return (coef_numer/coef_denom).toDouble() * kotlin.math.PI
    }
    constructor(numerator: Int, denominator: Int) {
        if (denominator == 0) throw IllegalArgumentException("Denominator cannot be zero")
        this.coef_numer = numerator
        this.coef_denom = denominator
    }
    public fun to_degree(): Degree {
        if (coef_denom == 0) throw IllegalArgumentException("Denominator cannot be zero")
        val degreeValue = (coef_numer.toDouble() / coef_denom) * 180 / kotlin.math.PI
        return Degree(degreeValue)
    }
}

private fun factorial(n: Int): Double {
    if (n < 0) throw IllegalArgumentException("Factorial is not defined for negative numbers")
    return if (n == 0) 1.0 else n * factorial(n - 1)
}

sealed class Operation {
    sealed class Binary : Operation() {
        object Add : Binary()
        object Subtract : Binary()
        object Multiply : Binary()
        object Divide : Binary()
        object Power : Binary()
        object Modulo : Binary()

        object Min : Unary() // Minimum of a set of numbers
        object Max : Unary() // Maximum of a set of numbers
        object Mean : Unary() // Arithmetic mean
        object Median : Unary() // Median of a set of numbers
        object Mode : Unary() // Mode of a set of numbers
        // object Variance : Unary() // Variance of a set of numbers
        object StdDev : Unary() // Standard deviation of a set of numbers


        object GCD : Binary() // Greatest Common Divisor
        object LCM : Binary() // Least Common Multiple

        object And : Unary() // Logical AND
        // object Or : Unary() // Logical OR
        object ShiftLeft : Binary() // Bitwise left shift
        // object ShiftRight : Binary() // Bitwise right shift
        // object Xor : Binary() // Bitwise XOR
        // object BitwiseAnd : Binary() // Bitwise AND
        // object BitwiseOr : Binary() // Bitwise OR
        // object BitwiseNot : Unary() // Bitwise NOT
        // object Nand : Binary() // Logical NAND
        // object Nor : Binary() // Logical NOR
        // object Xnor : Binary() // Logical XNOR
        // object Implication : Binary() // Logical implication (A implies B)
        // object Equivalence : Binary() // Logical equivalence (A if and only if B)

    }
    sealed class Unary : Operation() {
        object Not : Unary() // Logical NOT


        object Negate : Unary()
        object Sin : Unary()
        object Cos : Unary()
        object Tan : Unary()
        object Cot : Unary()
        object Sec : Unary()
        object Csc : Unary()

        object Arcsin : Unary()
        object Arccos : Unary()
        object Arctan : Unary()
        object Arccot : Unary()
        object Arcsec : Unary()
        object Arccsc : Unary()

        object Sinh : Unary()
        object Cosh : Unary()
        object Tanh : Unary()
        object Coth : Unary()
        object Sech : Unary()
        object Csch : Unary()

        object Asinh : Unary()
        object Acosh : Unary()
        object Atanh : Unary()
        object Acoth : Unary()
        object Asech : Unary()
        object Acsch : Unary()

        // object Degree : Unary()
        // object Radian : Unary()

        object Sqrt : Unary()
        object Ln : Unary()
        object Log10 : Unary()
        object Abs : Unary()
        // Combinatorics variants
        object Factorial : Unary()
        // object Choose: Unary()
        // object Permute: Unary()

        object IsPrime : Unary() // Check if a number is prime
        object IsEven : Unary() // Check if a number is even
        object IsOdd : Unary() // Check if a number is odd
        object Factors : Unary() // Get factors of a number

        // object Sign : Unary() // Sign function

        object Phi : Unary() // Golden ratio (phi)
        object Tau : Unary() // Tau constant (τ = 2π)
        object SQRT2 : Unary() // Square root of 2
        object SQRT3 : Unary() // Square root of 3
        object LN2 : Unary() // Natural logarithm of 2
        object LN10 : Unary() // Natural logarithm of 10


        object Floor : Unary()
        object Ceil : Unary()
        object Round : Unary()
        // object Signum : Unary()
        // object Trunc : Unary()
        // object Log2 : Unary()
        // object LogN : Unary() // Logarithm with base N
        object Exp : Unary() // Exponential function
        object Log1p : Unary() // Logarithm of (1 + x)
        // object Expm1 : Unary() // Exponential minus 1
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
                Operation.Unary.Cot -> 1 / kotlin.math.tan(operand)
                Operation.Unary.Sec -> 1 / kotlin.math.cos(operand)
                Operation.Unary.Csc -> 1 / kotlin.math.sin(operand)
                Operation.Unary.Arcsin -> kotlin.math.asin(operand)
                Operation.Unary.Arccos -> kotlin.math.acos(operand)
                Operation.Unary.Arctan -> kotlin.math.atan(operand)
                Operation.Unary.Arccot -> kotlin.math.atan(1 / operand)
                Operation.Unary.Arcsec -> kotlin.math.acos(1 / operand)
                Operation.Unary.Arccsc -> kotlin.math.asin(1 / operand)

                Operation.Unary.Sinh -> kotlin.math.sinh(operand)
                Operation.Unary.Cosh -> kotlin.math.cosh(operand)
                Operation.Unary.Tanh -> kotlin.math.tanh(operand)
                Operation.Unary.Coth -> 1 / kotlin.math.tanh(operand)
                Operation.Unary.Sech -> 1 / kotlin.math.cosh(operand)
                Operation.Unary.Csch -> 1 / kotlin.math.sinh(operand)

                Operation.Unary.Asinh -> kotlin.math.asinh(operand)
                Operation.Unary.Acosh -> {
                    if (operand < 1) throw ArithmeticException("Acosh is only defined for x >= 1")
                    kotlin.math.acosh(operand)
                }
                Operation.Unary.Atanh -> {
                    if (operand <= -1 || operand >= 1)
                            throw ArithmeticException("Atanh is only defined for -1 < x < 1")
                    kotlin.math.atanh(operand)
                }
                Operation.Unary.Acoth -> {
                    if (operand == 0.0) throw ArithmeticException("Acoth is undefined for zero")
                    kotlin.math.atanh(1 / operand)
                }
                Operation.Unary.Asech -> {
                    if (operand <= 0 || operand > 1)
                            throw ArithmeticException("Asech is only defined for 0 < x <= 1")
                    kotlin.math.acosh(1 / operand)
                }
                Operation.Unary.Acsch -> {
                    if (operand == 0.0) throw ArithmeticException("Acsch is undefined for zero")
                    kotlin.math.asinh(1 / operand)
                }

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
                Operation.Unary.Factorial -> {
                    if (operand < 0 || )
                            throw ArithmeticException("Factorial of negative or non-integer number")
                    factorial(operand.toInt())
                }
            }
}
