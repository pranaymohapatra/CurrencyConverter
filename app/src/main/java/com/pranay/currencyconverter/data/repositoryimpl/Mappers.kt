package com.pranay.currencyconverter.data.repositoryimpl

import com.pranay.currencyconverter.data.LatestRatesDTO
import com.pranay.currencyconverter.domain.ExchangeRates

fun LatestRatesDTO.mapToDomain() = ExchangeRates(base, rates)