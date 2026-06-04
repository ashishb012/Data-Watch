package com.ab.datawatch.di

import android.content.Context
import androidx.room.Room
import com.ab.datawatch.data.local.db.DataWatchDatabase
import com.ab.datawatch.data.local.db.UsageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DataWatchDatabase =
        Room.databaseBuilder(context, DataWatchDatabase::class.java, "datawatch.db")
            .build()

    @Provides
    fun provideUsageDao(db: DataWatchDatabase): UsageDao = db.usageDao()
}
