package com.robosoftin.movieapp.util

sealed class Resource<T> {
    class Idle<T> : Resource<T>()
    class Loading<T> : Resource<T>()
    data class Success<T>(
        val data: T,
        val message: String? = null,
    ) : Resource<T>()

    data class Error<T>(
        val errorData: String
    ) : Resource<T>()
}