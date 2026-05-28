/**
 * Generic result wrapper for domain operations.
 * Used to communicate success/error states between layers.
 *
 * Camada: Core / Common
 * Feature: All
 */
package com.drivepulse.core.common

/**
 * Sealed interface representing the result of an operation.
 *
 * @param T the type of data on success.
 */
sealed interface AppResult<out T> {
    /**
     * Operation completed successfully.
     * @property data the resulting data.
     */
    data class Success<T>(val data: T) : AppResult<T>

    /**
     * Operation failed with an error.
     * @property error the application error that occurred.
     */
    data class Error(val error: AppError) : AppResult<Nothing>
}

/**
 * Represents an application-level error.
 *
 * @property message human-readable error description.
 * @property throwable optional underlying exception.
 */
data class AppError(
    val message: String,
    val throwable: Throwable? = null
)
