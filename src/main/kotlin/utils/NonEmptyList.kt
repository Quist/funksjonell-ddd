package utils

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result

data class NonEmptyList<T> private constructor(val items: List<T>) {
    init {
        require(items.isNotEmpty()) { "The list cannot be empty" }
    }

    val first: T get() = items.first()

    companion object {
        fun <T> fromList(items: List<T>): Result<NonEmptyList<T>, String> {
            return if (items.isNotEmpty()) {
                Ok(NonEmptyList(items))
            } else {
                Err("The list cannot be empty")
            }
        }
    }
}