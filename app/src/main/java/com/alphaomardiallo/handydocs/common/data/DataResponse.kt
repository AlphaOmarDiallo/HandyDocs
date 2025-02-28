package com.alphaomardiallo.handydocs.common.data

sealed class DataResponse<T> {
    class Success<T>(val response: T) : DataResponse<T>()
    class Error<T>(val errorCode: Int?, val description: String?) : DataResponse<T>()
}
