package com.pranay.currencyconverter.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranay.currencyconverter.domain.CurrencyConverter
import com.pranay.currencyconverter.domain.ExchangeRates
import com.pranay.currencyconverter.domain.ExchangeRatesRepo
import com.pranay.currencyconverter.domain.helper.ResponseResult
import com.pranay.currencyconverter.domain.helper.launchOrError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainViewModel @Inject constructor(
    private val exchangeRatesRepo: ExchangeRatesRepo,
    @Named("IO_DISPATCHER") private val ioDispatcher: CoroutineDispatcher,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val KEY_SELECTION_STATE = "CURRENCY_SELECTION_STATE"
    }

    /*Since we are not observing exchange rates directly, this can be a simple object instead of stateflow
    This will get reinitialised every time app restarts, but not survive process death because
    we don't know when user will come back to app and by then rates might have changed*/
    lateinit var exchangeRates: ExchangeRates
        private set
    private val _convertedCurrency by lazy {
        MutableStateFlow(Converted(""))
    }
    private val currencyConverter: CurrencyConverter = CurrencyConverter()
    val convertedCurrency = _convertedCurrency.asStateFlow()

    //Using savedStateHandle to persist some UI state on bundle in-case of process death
    val currencySelectionState = savedStateHandle.getStateFlow(
        KEY_SELECTION_STATE,
        CurrencySelectionState(GlobalState(true, null), "1.00", "USD", "JPY", emptyList())
    )

    init {
        //Separating business logic from view logic, so that coverter can used else where also
        //we will fetch exchange rates on every app start
        refreshExchangeRates()
    }

    /*Ideally this function should be called before every conversion to get latest rates
    but is being avoided to make the logic simple. This can also be done with a config flag to
    enable/disable re-fetch in production code*/
    private fun refreshExchangeRates() {
        viewModelScope.launchOrError({
            withContext(ioDispatcher) {
                if (currencySelectionState.value.globalState.loading) {
                    savedStateHandle[KEY_SELECTION_STATE] =
                        currencySelectionState.value.copy(globalState = GlobalState(true, null))
                }
                when (val result = exchangeRatesRepo.getExchangeRates()) {
                    is ResponseResult.Success -> {
                        exchangeRates = result.data
                        savedStateHandle[KEY_SELECTION_STATE] =
                            currencySelectionState.value.copy(
                                globalState = GlobalState(false, null),
                                currencyList = exchangeRates.rates.keys.toList()
                            )
                        convert(currencySelectionState.value.inputAmount)
                    }

                    is ResponseResult.Error -> {
                        handleError(result.throwable ?: Exception("Something went wrong"))
                    }
                }
            }
        }, ::handleError)
    }

    private fun handleError(throwable: Throwable) {
        savedStateHandle[KEY_SELECTION_STATE] = currencySelectionState.value.copy(
            globalState = GlobalState(
                false,
                throwable.message ?: "Something went wrong, Please try again"
            )
        )
    }

    private fun convert(inputAmount: String) {
        //We want to convert only when input is not empty
        _convertedCurrency.value = if (inputAmount.isNotEmpty())
            with(currencySelectionState.value) {
                currencyConverter.convertCurrency(
                    inputAmount,
                    fromCurrency,
                    toCurrency,
                    exchangeRates.rates
                )
            }
        else
        //If input text is empty, we also want to clear the converted field text
            _convertedCurrency.value.copy(convertedValue = "")
    }

    fun setFromCurrency(currency: String) {
        if (currency != currencySelectionState.value.fromCurrency) {
            savedStateHandle[KEY_SELECTION_STATE] =
                currencySelectionState.value.copy(fromCurrency = currency)
            convert(currencySelectionState.value.inputAmount)
        }
    }

    fun setToCurrency(currency: String) {
        if (currency != currencySelectionState.value.toCurrency) {
            savedStateHandle[KEY_SELECTION_STATE] =
                currencySelectionState.value.copy(toCurrency = currency)
            convert(currencySelectionState.value.inputAmount)
        }
    }

    fun inputChanged(inputAmount: String) {
        var validInputAmount = currencySelectionState.value.inputAmount
        try {
            convert(inputAmount)
            //If conversion is success, update validInput to requested input
            validInputAmount = inputAmount
        } catch (exception: NumberFormatException) {
            exception.printStackTrace()
        } finally {
            savedStateHandle[KEY_SELECTION_STATE] =
                currencySelectionState.value.copy(inputAmount = validInputAmount)
        }
    }
}
