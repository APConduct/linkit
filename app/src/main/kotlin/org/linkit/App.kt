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

            // Format the result nicely
            val formattedResult =
                    if (result == result.toLong().toDouble()) {
                        // If it's a whole number, display without decimal
                        result.toLong().toString()
                    } else {
                        // Format to reasonable precision
                        "%.10g".format(result)
                    }

            println("  = $formattedResult")
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
  sin(x)         - Sine
  cos(x)         - Cosine
  tan(x)         - Tangent

Algebraic:
  sqrt(x)        - Square root
  abs(x)         - Absolute value

Logarithmic:
  ln(x)          - Natural logarithm
  log10(x)       - Base-10 logarithm

Usage: function_name(expression)
Example: sin(PI/2), sqrt(16), ln(E)
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

Usage: Use constants directly in expressions
Example: PI * 2, E^2
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

Mixed expressions:
  sqrt(16) + sin(PI/6)
  2 * PI * 5
  ln(E^3)
  abs(-42)

Try entering any of these expressions!
    """.trimIndent()
    )
}
