import java.sql.Array
// Set-theoretic definition of natural numbers
// as von Neumann ordinals
// 0={}         =θ,
// 1={0}        ={θ},
// 2={0, 1}     ={θ,{θ},
// 3={0,1,2}    ={θ,{θ},{θ{θ}}}
// 4={0,1,2,3}  ={θ,{θ},{θ{θ}},{θ{θ{θ}}}}
// and so on

// A set is empty if it contains no elements
// In Kotlin, we can represent an empty set using a custom class
// that implements the Set interface

// class EmptySet

// private fun <T> empty_set(): EmptySet {
//     return EmptySet()
// }

// // Now we can use this EmptySet class to represent an empty set in our code.

public sealed class SetElement<T> {
    data class Actual<T>(val value: T) : SetElement<T>() {
        override fun toString(): String = "ActualSet($value)"
        public fun is_empty(): Boolean {
            // Check is value is of type Unknown
            return value == null || (value is SetElement<*> && (value as SetElement<*>).is_empty())
        }
    }

    private fun is_empty(): Boolean {
        return when (this) {
            is Actual -> false // Actual set has a value
            is Empty -> true // Empty set has no elements
            is TrueSet<*> -> elements.isEmpty() // TrueSet checks if its elements are empty
        }
    }

    class Empty() : SetElement<Nothing>() {
        override fun toString(): String = "EmptySet"
        public fun is_empty(): Boolean = true
        public fun <T> pretend<T>(): SetElement<Nothing> {
            return Actual<Nothing>(null as Nothing)
        }`
    }

    private data class Settt<S>(val tt: SetElement<S>) : Set<Nothing> {
        override val size: Int
            get() = 0 // Empty set has no elements
        override fun containsAll(elements: Collection<Nothing>): Boolean = false
        override fun isEmpty(): Boolean = true
        override fun contains(element: Nothing): Boolean = false
        override fun iterator(): Iterator<Nothing> = emptyList<Nothing>().iterator()
        override fun toString(): String = "Hlp"
    }

    public fun toSet(): Set<T> {
        return when (this) {
            is Actual -> setOf(value)
            is Empty -> emptySet()
            is TrueSet<*> -> elements.mapNotNull { it as? Actual<T> }.map { it.value }.toSet()
        }
    }
}

public class TrueSet<T>(val value: T) : SetElement<SetElement<T>>() {
    public val elements: MutableSet<SetElement<T>> = mutableSetOf(SetElement.Actual(value))
}

public fun <T> SetElement<T>.toTrueSet(): TrueSet<T> {
    return when (this) {
        is SetElement.Actual -> TrueSet(value)
        is SetElement.Empty -> TrueSet(null as T)
        is TrueSet<*> -> this as TrueSet<T>
    }
}

public fun <T> SetElement<T>.isEmpty(): Boolean {
    return when (this) {
        is SetElement.Actual -> false // Actual set has a value
        is SetElement.Empty -> true // Empty set has no elements
        is TrueSet<*> -> elements.isEmpty() // TrueSet checks if its elements are empty
    }
}

public fun <T> SetElement<T>.isNotEmpty(): Boolean {
    return !isEmpty()
}

public fun <T> SetElement<T>.contains(element: T): Boolean {
    return when (this) {
        is SetElement.Actual -> value == element
        is SetElement.Empty -> false
        is TrueSet<*> -> elements.any { it is SetElement.Actual && it.value == element }
    }
}
