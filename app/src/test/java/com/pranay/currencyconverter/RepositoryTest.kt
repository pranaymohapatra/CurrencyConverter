package com.pranay.currencyconverter

import com.pranay.currencyconverter.data.CurrencyApi
import com.pranay.currencyconverter.data.OnlineCacheInterceptor
import com.pranay.currencyconverter.data.repositoryimpl.ExchangeRatesRepository
import com.pranay.currencyconverter.di.ApplicationModule
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockitoAnnotations
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.io.File
import java.time.Clock
import java.time.Instant
import java.util.concurrent.TimeUnit


class RepositoryTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: CurrencyApi
    private lateinit var exchangeRatesRepository: ExchangeRatesRepository
    private val fileHelper = FileHelper()
    private val cacheDirectory = File("src/test/resources/cache")
    private val cacheSize = 1 * 1024 * 1024
    private lateinit var okHttpClient: OkHttpClient


    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockWebServer = MockWebServer()
        okHttpClient = ApplicationModule.provideHttpClient(
            Cache(cacheDirectory, cacheSize.toLong()),
            OnlineCacheInterceptor(2, TimeUnit.SECONDS)
        )
        api = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build().create()
        exchangeRatesRepository = ExchangeRatesRepository(api)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test if products are parsed correctly is http returns response`() {
        runTest {
            val mockResponse = MockResponse()
            mockResponse.setBody(fileHelper.readFile("/response.json"))
            mockWebServer.enqueue(mockResponse)
            val parsedResponse = api.getLatestExchangeRates(anyString())
            Assert.assertEquals(169, parsedResponse.rates.size)
            Assert.assertEquals("USD", parsedResponse.base)
        }
    }

    @Test(expected = JsonDataException::class)
    fun `should throw parsing exception if http response is empty since we expect response cannot be blank`() {
        runTest {
            val mockResponse = MockResponse()
            mockResponse.setBody("{}")
            mockWebServer.enqueue(mockResponse)
            val parsedResponse = api.getLatestExchangeRates(anyString())
        }
    }

    @Test
    fun `cached response hit count should be 1 after second call`() {
        runTest {
            val mockResponse = MockResponse()
            mockResponse.setBody(fileHelper.readFile("/response.json"))
            mockWebServer.enqueue(mockResponse)
            async { api.getLatestExchangeRates(anyString()) }.await() //1st Call
            Assert.assertEquals(0, okHttpClient.cache?.hitCount())
            Assert.assertEquals(1, okHttpClient.cache?.requestCount())
            Assert.assertEquals(1, okHttpClient.cache?.networkCount())
            async { api.getLatestExchangeRates(anyString()) }.await() //2nd Call, now response is cached
            Assert.assertEquals(1, okHttpClient.cache?.hitCount())
            Assert.assertEquals(2, okHttpClient.cache?.requestCount())
            Assert.assertEquals(1, okHttpClient.cache?.networkCount())
        }
    }
}