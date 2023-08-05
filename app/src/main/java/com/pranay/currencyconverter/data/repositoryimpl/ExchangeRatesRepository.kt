package com.pranay.currencyconverter.data.repositoryimpl

import com.pranay.currencyconverter.data.CurrencyApi
import com.pranay.currencyconverter.domain.ExchangeRates
import com.pranay.currencyconverter.domain.ExchangeRatesRepo
import com.pranay.currencyconverter.domain.helper.ResponseResult
import kotlinx.coroutines.CancellationException
import javax.inject.Inject

class ExchangeRatesRepository @Inject constructor(private val currencyApi: CurrencyApi) : ExchangeRatesRepo {
    override suspend fun getExchangeRates(): ResponseResult<ExchangeRates> {
        return try {
            ResponseResult.Success(currencyApi.getLatestExchangeRates().mapToDomain())
        } catch (throwable: Throwable) {
            if (throwable is CancellationException)
                throw throwable
            else ResponseResult.Error(throwable.localizedMessage ?: "Error occurred", throwable)
        }
    }
}