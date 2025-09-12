package com.zyra.music.zyra.domain.utils

sealed interface DataError : Error {

    data object RequestTimeOut : DataError
    data object TooManyRequest : DataError
    data object NoInternet : DataError
    data object ServerError : DataError
    data object SerializationError : DataError
    data class UnknownError(val errorMessage : String? = null) : DataError
}