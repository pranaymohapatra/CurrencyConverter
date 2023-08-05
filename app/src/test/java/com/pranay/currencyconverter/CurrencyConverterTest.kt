package com.pranay.currencyconverter

import com.pranay.currencyconverter.domain.CurrencyConverter
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CurrencyConverterTest {

    private val rates = mapOf("USD" to 1f, "JPY" to 136.99234f, "INR" to 82.382f)
    private val currencyConverter = CurrencyConverter()

    @Test(expected = NumberFormatException::class)
    fun `for inputs containing non decimal characters, should throw number format exception`() {
        currencyConverter.convertCurrency("acs", "USD", "JPY", rates)
    }

    @Test(expected = NumberFormatException::class)
    fun `for empty inputs, should throw number format exception`() {
        currencyConverter.convertCurrency("", "USD", "JPY", rates)
    }

    @Test(expected = RuntimeException::class)
    fun `when selected currency is not found in rate map, should throw exception`() {
        //Here EUR is not in rates list
        currencyConverter.convertCurrency("2", "EUR", "JPY", rates)
    }
}