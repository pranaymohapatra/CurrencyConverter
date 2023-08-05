package com.pranay.currencyconverter

import androidx.lifecycle.SavedStateHandle
import com.pranay.currencyconverter.data.CurrencyApi
import com.pranay.currencyconverter.data.LatestRatesDTO
import com.pranay.currencyconverter.data.repositoryimpl.ExchangeRatesRepository
import com.pranay.currencyconverter.domain.CurrencyConverter
import com.pranay.currencyconverter.domain.ExchangeRatesRepo
import com.pranay.currencyconverter.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val testNameRule = TestName()

    @Mock
    private lateinit var api: CurrencyApi

    lateinit var mockedRepository: ExchangeRatesRepo

    private val savedStateHandle = SavedStateHandle()

    lateinit var mainViewModel: MainViewModel

    private val mockResponse = LatestRatesDTO(
        base = "USD",
        disclaimer = "https://openexchangerates.org/license",
        license = "https://openexchangerates.org/license",
        rates = mapOf("USD" to 1f, "JPY" to 136.99234f, "INR" to 82.382f),
        timestamp = 1684321200
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedRepository = ExchangeRatesRepository(api)
        //For all test except network error test, we need to init vm with correct value
        if (!testNameRule.methodName.equals("generate error when api fails")) {
            runTest(mainCoroutineRule.testDispatcher) {
                Mockito.`when`(api.getLatestExchangeRates(anyString()))
                    .thenReturn(mockResponse)
                mainViewModel =
                    MainViewModel(
                        mockedRepository,
                        mainCoroutineRule.testDispatcher,
                        savedStateHandle
                    )
            }
        }
    }

    @Test
    fun `generate error when api fails`() {
        runTest {
            Mockito.`when`(api.getLatestExchangeRates(anyString()))
                .thenThrow(RuntimeException("Network Failed"))
            mainViewModel =
                MainViewModel(mockedRepository, mainCoroutineRule.testDispatcher, savedStateHandle)
        }
        Assert.assertEquals(
            "Network Failed",
            mainViewModel.currencySelectionState.value.globalState.errorMessage
        )
    }

    @Test
    fun `set correct currency list size in currency selection state if api success`() {
        with(mainViewModel.currencySelectionState.value.currencyList) {
            Assert.assertEquals(mockResponse.rates.size, size)
            mockResponse.rates.keys.forEachIndexed { index, value ->
                Assert.assertEquals(value, this[index])
            }
        }
    }

    @Test
    fun `when input amount contains non decimal characters, input and converted amount should not update`() {
        val previousSetAmount = mainViewModel.currencySelectionState.value.inputAmount
        val previousConvertedAmount = mainViewModel.convertedCurrency.value.convertedValue
        //here a is invalid.
        // this can happen if user is pasting a text or if their IME doesn't support Number ime option
        mainViewModel.inputChanged("12ab")
        Assert.assertEquals(
            previousSetAmount,
            mainViewModel.currencySelectionState.value.inputAmount
        )
        Assert.assertEquals(
            previousConvertedAmount,
            mainViewModel.convertedCurrency.value.convertedValue
        )
    }

    @Test
    fun `when input is blank, input and converted amount should also be blank`() {
        mainViewModel.inputChanged("")
        Assert.assertEquals("", mainViewModel.currencySelectionState.value.inputAmount)
        Assert.assertEquals("", mainViewModel.convertedCurrency.value.convertedValue)
    }

    @Test
    fun `when input is valid number, correct converted amount should be updated`() {
        mainViewModel.inputChanged("279.56")
        val currencyState = mainViewModel.currencySelectionState.value
        val correctConversion =
            CurrencyConverter().convertCurrency(
                "279.56",
                currencyState.fromCurrency,
                currencyState.toCurrency,
                mainViewModel.exchangeRates.rates
            )
        Assert.assertEquals("279.56", mainViewModel.currencySelectionState.value.inputAmount)
        Assert.assertEquals(
            correctConversion.convertedValue,
            mainViewModel.convertedCurrency.value.convertedValue
        )
    }

    private fun assertCurrencySelectionResult() {
        val currencyState = mainViewModel.currencySelectionState.value
        val correctConversion =
            CurrencyConverter().convertCurrency(
                currencyState.inputAmount,
                currencyState.fromCurrency,
                currencyState.toCurrency,
                mainViewModel.exchangeRates.rates
            )
        Assert.assertEquals(
            correctConversion.exchangeRateText,
            mainViewModel.convertedCurrency.value.exchangeRateText
        )
        Assert.assertEquals(
            correctConversion.convertedValue,
            mainViewModel.convertedCurrency.value.convertedValue
        )
    }

    @Test
    fun `when To currency is changed, only converted amount and exchange rate text should change correctly`() {
        mainViewModel.setToCurrency("INR")
        assertCurrencySelectionResult()
    }

    @Test
    fun `when From currency is changed, only converted amount and exchange rate text should change correctly`() {
        mainViewModel.setFromCurrency("INR")
        assertCurrencySelectionResult()
    }
}