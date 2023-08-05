package com.pranay.currencyconverter.domain

import com.pranay.currencyconverter.presentation.viewmodel.Converted
import java.math.RoundingMode

class CurrencyConverter {
    fun convertCurrency(
        amount: String,
        from: String,
        to: String,
        exchangeRates: Map<String, Float>
    ): Converted {
        if (exchangeRates.containsKey(to) && exchangeRates.containsKey(from)) {
            val targetExchangeRate = exchangeRates[to]!!
            val baseExchangeRate = exchangeRates[from]!!
            val conversionFactor = targetExchangeRate / baseExchangeRate
            val convertedAmount =
                (amount.toDouble() * conversionFactor).toBigDecimal().setScale(3, RoundingMode.DOWN)
                    .toString()
            return Converted(convertedAmount, "1 $from = $conversionFactor $to")
        } else
            throw RuntimeException("Invalid Currency")
    }
}