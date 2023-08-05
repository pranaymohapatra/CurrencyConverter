package com.pranay.currencyconverter.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LatestRatesDTO(
    val base: String,
    val disclaimer: String,
    val license: String,
    val rates: Map<String,Float>,
    val timestamp: Int
)