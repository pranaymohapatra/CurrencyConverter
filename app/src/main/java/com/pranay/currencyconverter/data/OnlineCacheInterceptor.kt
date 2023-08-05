package com.pranay.currencyconverter.data

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OnlineCacheInterceptor @Inject constructor(
    private val cacheTime: Int,
    private val timeUnit: TimeUnit
) : Interceptor {
    constructor() : this(30, TimeUnit.MINUTES)

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val cacheControl = CacheControl.Builder()
            .maxAge(cacheTime, timeUnit)
            .build()
        return response.newBuilder()
            .header("Cache-Control", cacheControl.toString()) //from be
            .removeHeader("Pragma")
            .build()
    }
}