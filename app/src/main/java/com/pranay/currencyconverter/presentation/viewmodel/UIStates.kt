package com.pranay.currencyconverter.presentation.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Converted(val convertedValue: String, val exchangeRateText: String? = null): Parcelable

@Parcelize
data class CurrencySelectionState(
    val globalState: GlobalState,
    val inputAmount: String,
    val fromCurrency: String,
    val toCurrency: String,
    val currencyList: List<String>
): Parcelable