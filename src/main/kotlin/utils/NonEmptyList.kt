package utils

data class NonEmptyList<T> private constructor(val items: List<T>) {
    init {
        require(items.isNotEmpty()) { "The list cannot be empty" }
    }

    val first: T get() = items.first()

    companion object {
        fun <T> fromList(items: List<T>): NonEmptyList<T> {
            return if (items.isNotEmpty()) {
                NonEmptyList(items)
            } else {
                throw IllegalStateException("The list cannot be empty")
            }
        }
    }
}