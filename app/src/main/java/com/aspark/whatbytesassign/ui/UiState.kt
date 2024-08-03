package com.aspark.whatbytesassign.ui

import java.lang.Exception

sealed class UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val exception: Exception) : UiState<T>()
    data object Complete : UiState<Nothing>()
    data object Idle : UiState<Nothing>()
}