package com.zyra.music.zyra.domain.utils

fun DataError.getErrorMessage() : String{
    return  when(this){
        DataError.NoInternet -> "No internet connection. Please check your network and try again."
        DataError.RequestTimeOut -> "Request timed out. Please check your connection and try again."
        DataError.SerializationError -> "Data processing error. Please try again later."
        DataError.ServerError -> "Server error. Please try again after some time."
        DataError.TooManyRequest -> "Too many requests. Please wait and try again later."
        is DataError.UnknownError -> "Something went wrong. Please try again later. Error message : ${this.errorMessage}"
    }
}