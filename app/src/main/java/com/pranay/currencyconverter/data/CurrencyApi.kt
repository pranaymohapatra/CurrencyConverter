package com.pranay.currencyconverter.data

import com.pranay.currencyconverter.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryName

interface CurrencyApi {
    @GET("latest.json")
    suspend fun getLatestExchangeRates(@Query("app_id") appId:String = BuildConfig.API_KEY) : LatestRatesDTO
}