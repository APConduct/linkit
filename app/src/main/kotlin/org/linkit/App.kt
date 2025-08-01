/*
 * Advanced Calculator CLI Application
 */
package org.linkit

fun main() {
    println("🧮 Advanced Calculator")
    println("====================")
    println("Enter mathematical expressions or 'help' for commands")
    println("Type 'quit' or 'exit' to close")
    println()

    val calc = Calc()
    val parser = Parser()

    while (true) {
        print("calc> ")
        System.out.flush() // Ensure prompt is displayed
        val input =
                try {
                    readLine()?.trim() ?: break
                } catch (e: Exception) {
                    // If readLine fails, try alternative approach
                    System.`in`.bufferedReader().readLine()?.trim() ?: break
                }

        when (input.lowercase()) {
            "", " " -> continue
            "quit", "exit", "q" -> {
                println("Goodbye! 👋")
                break
            }
            "help", "h" -> {
                showHelp()
                continue
            }
            "functions", "funcs" -> {
                showFunctions()
                continue
            }
            "constants", "const" -> {
                showConstants()
                continue
            }
            "examples", "ex" -> {
                showExamples()
                continue
            }
            "radians", "rad" -> {
                calc.setAngleMode(AngleMode.RADIANS)
                println("📐 Angle mode set to RADIANS")
                println("   Trig functions now expect/return radians")
                continue
            }
            "degrees", "deg" -> {
                calc.setAngleMode(AngleMode.DEGREES)
                println("📐 Angle mode set to DEGREES")
                println("   Trig functions now expect/return degrees")
                continue
            }
            "mode" -> {
                val currentMode = calc.getAngleMode()
                println("📐 Current angle mode: $currentMode")
                println("   Use 'degrees' or 'radians' to change")
                continue
            }
            "clear", "cls" -> {
                // Clear screen (works on most terminals)
                print("\u001b[2J\u001b[H")
                println("🧮 Advanced Calculator")
                println("====================")
                continue
            }
        }

        try {
            val expr = parser.parse(input)
            val result = calc.eval(expr)

            // Format the result with precise display when possible
            val preciseFormat = PreciseDisplay.formatValue(result, calc.getAngleMode())
            val angleDescription = PreciseDisplay.getAngleDescription(result, calc.getAngleMode())

            if (angleDescription != null && PreciseDisplay.isNiceAngle(result, calc.getAngleMode())
            ) {
                println("  = $preciseFormat ($angleDescription)")
            } else {
                println("  = $preciseFormat")
            }
        } catch (e: ParseException) {
            println("❌ Parse Error: ${e.message}")
        } catch (e: ArithmeticException) {
            println("❌ Math Error: ${e.message}")
        } catch (e: IllegalArgumentException) {
            println("❌ Error: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected Error: ${e.message}")
        }
    }
}

private fun showHelp() {
    println(
            """
📖 Calculator Help
================

Commands:
  help, h        - Show this help
  functions      - List available functions
  constants      - List available constants
  examples       - Show example expressions
  mode           - Show current angle mode
  degrees, deg   - Set angle mode to degrees
  radians, rad   - Set angle mode to radians
  clear, cls     - Clear screen
  quit, exit, q  - Exit calculator

Basic Operations:
  +, -, *, /     - Addition, subtraction, multiplication, division
  ^              - Exponentiation (power)
  %              - Modulo (remainder)

Parentheses () are supported for grouping expressions.

Type 'functions' to see available mathematical functions.
    """.trimIndent()
    )
}

private fun showFunctions() {
    println(
            """
🔧 Available Functions
====================

Trigonometric:
  sin(x), cos(x), tan(x)     - Basic trig functions
  cot(x), sec(x), csc(x)     - Other trig functions
  asin(x), acos(x), atan(x)  - Inverse trig functions
  acot(x), asec(x), acsc(x)  - More inverse trig functions

Hyperbolic:
  sinh(x), cosh(x), tanh(x)  - Hyperbolic functions
  coth(x), sech(x), csch(x)  - Other hyperbolic functions
  asinh(x), acosh(x), atanh(x) - Inverse hyperbolic functions
  acoth(x), asech(x), acsch(x) - More inverse hyperbolic functions

Algebraic:
  sqrt(x)        - Square root
  abs(x)         - Absolute value
  sign(x)        - Sign function (-1, 0, 1)
  floor(x)       - Floor (round down)
  ceil(x)        - Ceiling (round up)
  round(x)       - Round to nearest integer

Angle Conversion:
  rads(x)        - Convert degrees to radians
  degs(x)        - Convert radians to degrees

Logarithmic:
  ln(x)          - Natural logarithm
  log10(x)       - Base-10 logarithm
  log2(x)        - Base-2 logarithm
  exp(x)         - Exponential (e^x)
  log1p(x)       - ln(1 + x)

Number Theory:
  fact(x)        - Factorial
  isprime(x)     - Check if prime (returns 1 or 0)
  iseven(x)      - Check if even (returns 1 or 0)
  isodd(x)       - Check if odd (returns 1 or 0)
  factors(x)     - Count of factors

Logic:
  not(x)         - Logical NOT (0 becomes 1, non-zero becomes 0)

Usage: function_name(expression)
Example: sin(PI/2), sqrt(16), fact(5), rads(90), degs(PI)
    """.trimIndent()
    )
}

private fun showConstants() {
    println(
            """
📐 Available Constants
====================

  PI             - π ≈ 3.14159...
  E              - Euler's number ≈ 2.71828...
  PHI            - Golden ratio ≈ 1.61803...
  TAU            - τ = 2π ≈ 6.28318...
  SQRT2          - √2 ≈ 1.41421...
  SQRT3          - √3 ≈ 1.73205...
  LN2            - ln(2) ≈ 0.69314...
  LN10           - ln(10) ≈ 2.30258...
  RIGHT_ANGLE    - π/2 ≈ 1.57079...
  STRAIGHT_ANGLE - π ≈ 3.14159...
  FULL_CIRCLE    - 2π ≈ 6.28318...

Usage: Use constants directly in expressions
Example: PI * 2, E^2, PHI * 5, RIGHT_ANGLE * 2

Note: Angle mode affects trig functions - use 'mode' to check current setting
    """.trimIndent()
    )
}

private fun showExamples() {
    println(
            """
💡 Example Expressions
======================

Basic arithmetic:
  2 + 3 * 4
  (5 + 3) * 2
  10 / 2 - 1

Powers and roots:
  2^8
  sqrt(144)
  3^(1/2)

Trigonometry:
  sin(PI/2)
  cos(0)
  tan(PI/4)
  asin(0.5)

Hyperbolic functions:
  sinh(1)
  cosh(0)
  tanh(2)

Number theory:
  fact(5)
  isprime(17)
  iseven(42)
  factors(12)

Logarithms:
  ln(E)
  log10(100)
  log2(8)
  exp(1)

Rounding:
  floor(3.7)
  ceil(3.2)
  round(3.5)

Angle conversion:
  rads(90)
  degs(PI)
  sin(rads(30))
  cos(rads(60))
  tan(rads(45))
  degs(rads(180))

Mode-dependent examples (try 'degrees' then 'radians'):
  sin(30)        - Result depends on current mode
  cos(60)        - 0.5 in degrees, different in radians
  asin(0.5)      - Returns 30 in degrees, π/6 in radians

Mixed expressions:
  sqrt(16) + sin(PI/6)
  fact(4) + log2(8)
  PHI * TAU / 2
  abs(-42) + sign(-5)
  sin(rads(30)) + cos(rads(60))
  asin(sin(rads(45)))

Try entering any of these expressions!
    """.trimIndent()
    )
}
