package org.linkit

import kotlin.math.*

/**
 * Provides precise fractional display for common mathematical values, especially angles and their
 * relationships to π.
 */
object PreciseDisplay {

    /** Common angle values in radians as fractions of π */
    private val commonAngleRadians =
            mapOf(
                    0.0 to "0",
                    PI / 6 to "π/6",
                    PI / 4 to "π/4",
                    PI / 3 to "π/3",
                    PI / 2 to "π/2",
                    2 * PI / 3 to "2π/3",
                    3 * PI / 4 to "3π/4",
                    5 * PI / 6 to "5π/6",
                    PI to "π",
                    7 * PI / 6 to "7π/6",
                    5 * PI / 4 to "5π/4",
                    4 * PI / 3 to "4π/3",
                    3 * PI / 2 to "3π/2",
                    5 * PI / 3 to "5π/3",
                    7 * PI / 4 to "7π/4",
                    11 * PI / 6 to "11π/6",
                    2 * PI to "2π",
                    -PI / 6 to "-π/6",
                    -PI / 4 to "-π/4",
                    -PI / 3 to "-π/3",
                    -PI / 2 to "-π/2",
                    -2 * PI / 3 to "-2π/3",
                    -3 * PI / 4 to "-3π/4",
                    -5 * PI / 6 to "-5π/6",
                    -PI to "-π"
            )

    /** Common angle values in degrees */
    private val commonAnglesDegrees =
            mapOf(
                    0.0 to "0°",
                    30.0 to "30°",
                    45.0 to "45°",
                    60.0 to "60°",
                    90.0 to "90°",
                    120.0 to "120°",
                    135.0 to "135°",
                    150.0 to "150°",
                    180.0 to "180°",
                    210.0 to "210°",
                    225.0 to "225°",
                    240.0 to "240°",
                    270.0 to "270°",
                    300.0 to "300°",
                    315.0 to "315°",
                    330.0 to "330°",
                    360.0 to "360°",
                    -30.0 to "-30°",
                    -45.0 to "-45°",
                    -60.0 to "-60°",
                    -90.0 to "-90°",
                    -120.0 to "-120°",
                    -135.0 to "-135°",
                    -150.0 to "-150°",
                    -180.0 to "-180°"
            )

    /** Common mathematical constants */
    private val commonConstants =
            mapOf(
                    PI to "π",
                    E to "e",
                    2 * PI to "2π",
                    PI / 2 to "π/2",
                    PI / 3 to "π/3",
                    PI / 4 to "π/4",
                    PI / 6 to "π/6",
                    sqrt(2.0) to "√2",
                    sqrt(3.0) to "√3",
                    sqrt(5.0) to "√5",
                    (1 + sqrt(5.0)) / 2 to "φ", // Golden ratio
                    ln(2.0) to "ln(2)",
                    ln(10.0) to "ln(10)",
                    1.0 to "1",
                    0.0 to "0",
                    -1.0 to "-1",
                    0.5 to "1/2",
                    0.25 to "1/4",
                    0.75 to "3/4",
                    1.0 / 3.0 to "1/3",
                    2.0 / 3.0 to "2/3"
            )

    /** Tolerance for floating point comparison */
    private const val TOLERANCE = 1e-10

    /**
     * Attempts to display a value in precise fractional form if it matches a known common value,
     * otherwise returns the decimal representation.
     */
    fun formatValue(value: Double, angleMode: AngleMode = AngleMode.RADIANS): String {
        // First check for exact matches with constants
        commonConstants.forEach { (constantValue, display) ->
            if (abs(value - constantValue) < TOLERANCE) {
                return display
            }
        }

        // Check for angle-specific formatting
        when (angleMode) {
            AngleMode.RADIANS -> {
                commonAngleRadians.forEach { (angleValue, display) ->
                    if (abs(value - angleValue) < TOLERANCE) {
                        return display
                    }
                }

                // Check for simple multiples of π
                val piMultiple = value / PI
                if (abs(piMultiple - piMultiple.toInt()) < TOLERANCE) {
                    val intMultiple = piMultiple.toInt()
                    return when (intMultiple) {
                        0 -> "0"
                        1 -> "π"
                        -1 -> "-π"
                        else -> "${intMultiple}π"
                    }
                }

                // Check for simple fractions of π
                for (denominator in 2..12) {
                    val numerator = (value * denominator / PI).round()
                    if (abs(value - (numerator * PI / denominator)) < TOLERANCE) {
                        return when {
                            numerator == 0.0 -> "0"
                            numerator == denominator.toDouble() -> "π"
                            numerator == -denominator.toDouble() -> "-π"
                            numerator == 1.0 -> "π/$denominator"
                            numerator == -1.0 -> "-π/$denominator"
                            else -> "${numerator.toInt()}π/$denominator"
                        }
                    }
                }
            }
            AngleMode.DEGREES -> {
                commonAnglesDegrees.forEach { (angleValue, display) ->
                    if (abs(value - angleValue) < TOLERANCE) {
                        return display
                    }
                }

                // Check if it's a nice integer degree value
                if (abs(value - value.round()) < TOLERANCE) {
                    return "${value.round().toInt()}°"
                }
            }
        }

        // Check for simple fractions
        for (denominator in 2..16) {
            val numerator = (value * denominator).round()
            if (abs(value - (numerator / denominator)) < TOLERANCE) {
                return when {
                    numerator == 0.0 -> "0"
                    numerator == denominator.toDouble() -> "1"
                    numerator == -denominator.toDouble() -> "-1"
                    denominator == 1 -> numerator.toInt().toString()
                    else -> {
                        val gcd = gcd(abs(numerator.toInt()), denominator)
                        val simplifiedNum = numerator.toInt() / gcd
                        val simplifiedDen = denominator / gcd
                        if (simplifiedDen == 1) {
                            simplifiedNum.toString()
                        } else {
                            "$simplifiedNum/$simplifiedDen"
                        }
                    }
                }
            }
        }

        // Check for square roots of small integers
        for (i in 2..20) {
            if (abs(value - sqrt(i.toDouble())) < TOLERANCE) {
                return "√$i"
            }
            if (abs(value + sqrt(i.toDouble())) < TOLERANCE) {
                return "-√$i"
            }
        }

        // Fall back to decimal representation
        return formatDecimal(value)
    }

    /** Formats a decimal number with appropriate precision */
    private fun formatDecimal(value: Double): String {
        return when {
            abs(value) < 1e-10 -> "0"
            value == value.toLong().toDouble() -> value.toLong().toString()
            abs(value) > 1e6 || abs(value) < 1e-4 -> "%.6e".format(value)
            else -> "%.10g".format(value)
        }
    }

    /** Extension function to round a double to the nearest integer */
    private fun Double.round(): Double = kotlin.math.round(this)

    /** Helper function for GCD calculation */
    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }

    /** Checks if a value represents a "nice" angle in the current mode */
    fun isNiceAngle(value: Double, angleMode: AngleMode): Boolean {
        return when (angleMode) {
            AngleMode.RADIANS -> commonAngleRadians.any { abs(value - it.key) < TOLERANCE }
            AngleMode.DEGREES -> commonAnglesDegrees.any { abs(value - it.key) < TOLERANCE }
        }
    }

    /** Gets a descriptive name for common angles */
    fun getAngleDescription(value: Double, angleMode: AngleMode): String? {
        val formatted = formatValue(value, angleMode)
        return when {
            formatted == "0" || formatted == "0°" -> "zero angle"
            formatted == "π/2" || formatted == "90°" -> "right angle"
            formatted == "π" || formatted == "180°" -> "straight angle"
            formatted == "2π" || formatted == "360°" -> "full circle"
            formatted == "π/4" || formatted == "45°" -> "half right angle"
            formatted == "π/3" || formatted == "60°" -> "acute angle"
            formatted == "π/6" || formatted == "30°" -> "acute angle"
            else -> null
        }
    }
}
