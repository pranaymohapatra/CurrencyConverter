package com.pranay.currencyconverter.domain.helper

sealed class ResponseResult<out R> {
    data class Success<out T>(val data: T) : ResponseResult<T>()
    data class Error(val message: String?, val throwable: Throwable? = null) :
        ResponseResult<Nothing>()
}