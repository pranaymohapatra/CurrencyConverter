package com.pranay.currencyconverter.di

import com.pranay.currencyconverter.data.repositoryimpl.ExchangeRatesRepository
import com.pranay.currencyconverter.domain.ExchangeRatesRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindExchangeRatesRepository(exchangeRatesRepository: ExchangeRatesRepository)
            : ExchangeRatesRepo
}