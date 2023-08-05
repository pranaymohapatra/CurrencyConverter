package com.pranay.currencyconverter.di

import android.content.Context
import com.pranay.currencyconverter.BuildConfig
import com.pranay.currencyconverter.data.CurrencyApi
import com.pranay.currencyconverter.data.OnlineCacheInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {
    @Provides
    @Singleton
    fun provideCurrencyApi(httpClient: OkHttpClient): CurrencyApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create()
    }

    @Provides
    @Singleton
    fun provideCacheInstance(@ApplicationContext context: Context): Cache {
        val cacheSize = 1 * 1024 * 1024 // 2 MB Cache
        return Cache(context.cacheDir, cacheSize.toLong())
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        cache: Cache,
        onlineCacheInterceptor: OnlineCacheInterceptor
    ): OkHttpClient {
        return OkHttpClient
            .Builder()
            .cache(cache)
            .addNetworkInterceptor(onlineCacheInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor(): OnlineCacheInterceptor {
        return OnlineCacheInterceptor(30, TimeUnit.MINUTES)
    }

    @Provides
    @Singleton
    @Named("IO_DISPATCHER")
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}