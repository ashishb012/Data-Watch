package com.ab.datawatch.di

import com.ab.datawatch.data.repository.NetworkStatsRepository
import com.ab.datawatch.data.repository.NetworkStatsRepositoryImpl
import com.ab.datawatch.data.repository.UserPreferencesRepository
import com.ab.datawatch.data.repository.UserPreferencesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNetworkStatsRepository(
        impl: NetworkStatsRepositoryImpl
    ): NetworkStatsRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository
}
