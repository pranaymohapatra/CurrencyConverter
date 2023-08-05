package com.pranay.currencyconverter.domain

data class ExchangeRates (val base: String, val rates: Map<String,Float>)