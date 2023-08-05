package com.pranay.currencyconverter.presentation.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GlobalState(val loading: Boolean, val errorMessage: String? = null): Parcelable