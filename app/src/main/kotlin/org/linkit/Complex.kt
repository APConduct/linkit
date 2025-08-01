class i

class Complex(val real: Double, val imaginary: Double) {

    operator fun plus(other: Complex): Complex {
        return Complex(this.real + other.real, this.imaginary + other.imaginary)
    }

    operator fun minus(other: Complex): Complex {
        return Complex(this.real - other.real, this.imaginary - other.imaginary)
    }

    operator fun times(other: Complex): Complex {
        return Complex(
                this.real * other.real - this.imaginary * other.imaginary,
                this.real * other.imaginary + this.imaginary * other.real
        )
    }

    override fun toString(): String {
        return "$real + ${imaginary}i"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Complex) return false
        return this.real == other.real && this.imaginary == other.imaginary
    }

    override fun hashCode(): Int {
        var result = real.hashCode()
        result = 31 * result + imaginary.hashCode()
        return result
    }

    fun magnitude(): Double {
        return kotlin.math.sqrt(real * real + imaginary * imaginary)
    }

    fun phase(): Double {
        return kotlin.math.atan2(imaginary, real)
    }

    fun conjugate(): Complex {
        return Complex(real, -imaginary)
    }

    fun reciprocal(): Complex {
        val denom = real * real + imaginary * imaginary
        if (denom == 0.0) throw ArithmeticException("Cannot divide by zero")
        return Complex(real / denom, -imaginary / denom)
    }

    operator fun div(other: Complex): Complex {
        return this * other.reciprocal()
    }

    fun polar(): Pair<Double, Double> {
        return Pair(magnitude(), phase())
    }

    companion object {
        fun fromPolar(magnitude: Double, phase: Double): Complex {
            return Complex(magnitude * kotlin.math.cos(phase), magnitude * kotlin.math.sin(phase))
        }
    }
}
