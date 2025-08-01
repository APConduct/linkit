package org.linkit

import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

class Degree {
    var value: Double = 0.0
    constructor(value: Double) {
        this.value = value
    }
    constructor(value: Int) {
        this.value = value.toDouble()
    }
    @Suppress("UNUSED_VARIABLE")
    public fun to_radian(): Radian {
        // Remove the 0-360 restriction to allow any degree value
        val radianValue = value * kotlin.math.PI / 180
        // Convert to fraction representation for precision
        val denominator = 180
        val numerator = (value * kotlin.math.PI).toInt()
        return Radian(numerator, denominator)
    }
}

//// Represents an angle in radians, with numerator and denominator for precision
// This class allows for precise representation of angles in radians, useful for trigonometric
// calculations
// It supports operations like conversion to degrees and evaluation of trigonometric functions
// The value is calculated as (numerator/denominator) * π, ensuring high precision
// Usage: Radian(numerator, denominator)
class Radian {
    var coef_numer: Int = 0
    var coef_denom: Int = 1
    public fun value(): Double {
        return (coef_numer / coef_denom).toDouble() * kotlin.math.PI
    }
    constructor(numerator: Int, denominator: Int) {
        if (denominator == 0) throw IllegalArgumentException("Denominator cannot be zero")
        this.coef_numer = numerator
        this.coef_denom = denominator
    }
    public fun to_degree(): Degree {
        if (coef_denom == 0) throw IllegalArgumentException("Denominator cannot be zero")
        val degreeValue = (coef_numer.toDouble() / coef_denom) * 180
        return Degree(degreeValue)
    }
}

private fun factorial(n: Int): Double {
    if (n < 0) throw IllegalArgumentException("Factorial is not defined for negative numbers")
    return if (n == 0) 1.0 else n * factorial(n - 1)
}

private fun gcd(a: Int, b: Int): Int {
    if (b == 0) return a
    return gcd(b, a % b)
}

enum class AngleMode {
    RADIANS,
    DEGREES
}

sealed class Operation {
    sealed class Binary : Operation() {
        object Add : Binary()
        object Subtract : Binary()
        object Multiply : Binary()
        object Divide : Binary()
        object Power : Binary()
        object Modulo : Binary()

        object Min : Binary() // Minimum of a set of numbers
        object Max : Binary() // Maximum of a set of numbers
        object Mean : Binary() // Arithmetic mean
        object Median : Binary() // Median of a set of numbers
        object Mode : Binary() // Mode of a set of numbers
        object Variance : Binary() // Variance of a set of numbers
        object StdDev : Binary() // Standard deviation of a set of numbers

        object GCD : Binary() // Greatest Common Divisor
        object LCM : Binary() // Least Common Multiple

        object And : Binary() // Logical AND
        object ShiftLeft : Binary() // Bitwise left shift
        object ShiftRight : Binary() // Bitwise right shift
        // object Xor : Binary() // Bitwise XOR
        // object BitwiseAnd : Binary() // Bitwise AND
        // object BitwiseOr : Binary() // Bitwise OR
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

        object Sign : Unary() // Sign function

        object Floor : Unary()
        object Ceil : Unary()
        object Round : Unary()
        // object Signum : Unary()
        // object Trunc : Unary()
        object Log2 : Unary()
        object LogN : Unary() // Logarithm with base N
        object Exp : Unary() // Exponential function
        object Log1p : Unary() // Logarithm of (1 + x)
        // object Expm1 : Unary() // Exponential minus 1

        // Angle conversion functions
        object Rads : Unary() // Convert degrees to radians
        object Degs : Unary() // Convert radians to degrees
    }
}

sealed class Expr {
    data class Number(val value: Double) : Expr()
    data class BinaryOp(val left: Expr, val op: Operation.Binary, val right: Expr) : Expr()
    data class UnaryOp(val op: Operation.Unary, val operand: Expr) : Expr()
    data class Variable(val name: String) : Expr()
}

class Calc {
    private var angleMode = AngleMode.RADIANS

    private val constants =
            mapOf(
                    "PI" to kotlin.math.PI,
                    "E" to kotlin.math.E,
                    "PHI" to (1 + kotlin.math.sqrt(5.0)) / 2,
                    "TAU" to 2 * kotlin.math.PI,
                    "SQRT2" to kotlin.math.sqrt(2.0),
                    "SQRT3" to kotlin.math.sqrt(3.0),
                    "LN2" to kotlin.math.ln(2.0),
                    "LN10" to kotlin.math.ln(10.0),
                    "RIGHT_ANGLE" to kotlin.math.PI / 2,
                    "STRAIGHT_ANGLE" to kotlin.math.PI,
                    "FULL_CIRCLE" to 2 * kotlin.math.PI
            )

    fun setAngleMode(mode: AngleMode) {
        angleMode = mode
    }

    fun getAngleMode(): AngleMode = angleMode

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
                Operation.Binary.Min -> kotlin.math.min(left, right)
                Operation.Binary.Max -> kotlin.math.max(left, right)
                Operation.Binary.Mean -> (left + right) / 2
                Operation.Binary.Median -> (left + right) / 2 // Simplified for two numbers
                Operation.Binary.Mode ->
                        left // Mode is not defined for two numbers, returning left as default
                Operation.Binary.Variance ->
                        throw NotImplementedError("Variance calculation not implemented")
                Operation.Binary.StdDev ->
                        kotlin.math.sqrt((left - right).pow(2) / 2) // Simplified for two numbers
                Operation.Binary.GCD -> {
                    if (left < 0 || right < 0)
                            throw ArithmeticException("GCD is not defined for negative numbers")
                    gcd(left.toInt(), right.toInt()).toDouble()
                }
                Operation.Binary.LCM -> {
                    if (left < 0 || right < 0)
                            throw ArithmeticException("LCM is not defined for negative numbers")
                    (left * right / gcd(left.toInt(), right.toInt())).toDouble()
                }
                Operation.Binary.And -> if (left != 0.0 && right != 0.0) 1.0 else 0.0
                // Operation.Binary.Or -> if (left != 0.0 || right != 0
                Operation.Binary.ShiftLeft -> {
                    if (left < 0 || right < 0)
                            throw ArithmeticException(
                                    "Shift operations are not defined for negative numbers"
                            )
                    (left.toInt() shl right.toInt()).toDouble()
                }
                Operation.Binary.ShiftRight -> {
                    if (left < 0 || right < 0)
                            throw ArithmeticException(
                                    "Shift operations are not defined for negative numbers"
                            )
                    (left.toInt() shr right.toInt()).toDouble()
                }
            }

    private fun evaluate_unary(op: Operation.Unary, operand: Double): Double =
            when (op) {
                Operation.Unary.Not -> if (operand == 0.0) 1.0 else 0.0 // Logical NOT
                Operation.Unary.Log2 -> {
                    if (operand <= 0) throw ArithmeticException("Logarithm of non-positive number")
                    kotlin.math.log2(operand)
                }
                Operation.Unary.LogN -> {
                    if (operand <= 0) throw ArithmeticException("Logarithm of non-positive number")
                    kotlin.math.log(operand, E) // Natural logarithm as default
                }
                Operation.Unary.Sign -> {
                    when {
                        operand > 0 -> 1.0
                        operand < 0 -> -1.0
                        else -> 0.0
                    }
                }
                Operation.Unary.Negate -> -operand
                Operation.Unary.Sin -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    kotlin.math.sin(angleInRadians)
                }
                Operation.Unary.Cos -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    kotlin.math.cos(angleInRadians)
                }
                Operation.Unary.Tan -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    kotlin.math.tan(angleInRadians)
                }
                Operation.Unary.Cot -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    1 / kotlin.math.tan(angleInRadians)
                }
                Operation.Unary.Sec -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    1 / kotlin.math.cos(angleInRadians)
                }
                Operation.Unary.Csc -> {
                    val angleInRadians =
                            if (angleMode == AngleMode.DEGREES) {
                                operand * kotlin.math.PI / 180.0
                            } else {
                                operand
                            }
                    1 / kotlin.math.sin(angleInRadians)
                }
                Operation.Unary.Arcsin -> {
                    val result = kotlin.math.asin(operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
                Operation.Unary.Arccos -> {
                    val result = kotlin.math.acos(operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
                Operation.Unary.Arctan -> {
                    val result = kotlin.math.atan(operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
                Operation.Unary.Arccot -> {
                    val result = kotlin.math.atan(1 / operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
                Operation.Unary.Arcsec -> {
                    val result = kotlin.math.acos(1 / operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
                Operation.Unary.Arccsc -> {
                    val result = kotlin.math.asin(1 / operand)
                    if (angleMode == AngleMode.DEGREES) {
                        result * 180.0 / kotlin.math.PI
                    } else {
                        result
                    }
                }
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
                    if (operand < 0 || operand != operand.toInt().toDouble())
                            throw ArithmeticException("Factorial of negative or non-integer number")
                    factorial(operand.toInt())
                }
                Operation.Unary.IsPrime -> {
                    if (operand != operand.toInt().toDouble() || operand < 2) {
                        0.0 // Not prime
                    } else if (operand == 2.0) {
                        1.0 // Prime
                    } else if (operand % 2 == 0.0) {
                        0.0 // Even numbers > 2 are not prime
                    } else {
                        var isPrime = true
                        for (i in 3..sqrt(operand).toInt() step 2) {
                            if (operand % i == 0.0) {
                                isPrime = false
                                break
                            }
                        }
                        if (isPrime) 1.0 else 0.0
                    }
                }
                Operation.Unary.IsEven -> {
                    if (operand != operand.toInt().toDouble()) 0.0
                    else if (operand % 2 == 0.0) 1.0 else 0.0
                }
                Operation.Unary.IsOdd -> {
                    if (operand != operand.toInt().toDouble()) 0.0
                    else if (operand % 2 != 0.0) 1.0 else 0.0
                }
                Operation.Unary.Factors -> {
                    if (operand < 1 || operand != operand.toInt().toDouble())
                            throw ArithmeticException(
                                    "Factors are only defined for positive integers"
                            )
                    var count = 0.0
                    for (i in 1..operand.toInt()) {
                        if (operand % i == 0.0) {
                            count++
                        }
                    }
                    count
                }
                Operation.Unary.Floor -> kotlin.math.floor(operand)
                Operation.Unary.Ceil -> kotlin.math.ceil(operand)
                Operation.Unary.Round -> kotlin.math.round(operand).toDouble()
                // Operation.Unary.Signum -> kotlin.math.sign(operand)
                // Operation.Unary.Trunc -> operand.toInt().toDouble() // Truncate to Int
                // Operation.Unary.Log2 -> kotlin.math.log2(operand)
                // Operation.Unary.LogN -> {
                //     if (operand <= 0) throw ArithmeticException("Logarithm of non-positive
                // number")
                //     kotlin.math.log(operand, N) // Logarithm with base N
                // }
                Operation.Unary.Exp -> kotlin.math.exp(operand) // Exponential function
                // Operation.Unary.Expm1 -> kotlin.math.expm1(operand) // Exponential minus 1
                //
                // // Logarithm of (1 + x)
                Operation.Unary.Log1p -> {
                    if (operand <= -1)
                            throw ArithmeticException(
                                    "Logarithm of (1 + x) is undefined for x <= -1"
                            )
                    kotlin.math.ln(1 + operand)
                }
                Operation.Unary.Rads -> {
                    // Convert degrees to radians
                    operand * kotlin.math.PI / 180.0
                }
                Operation.Unary.Degs -> {
                    // Convert radians to degrees
                    operand * 180.0 / kotlin.math.PI
                }
            }
}
