package com.pranay.currencyconverter.domain

import com.pranay.currencyconverter.domain.helper.ResponseResult

interface ExchangeRatesRepo {
    suspend fun getExchangeRates() : ResponseResult<ExchangeRates>
}