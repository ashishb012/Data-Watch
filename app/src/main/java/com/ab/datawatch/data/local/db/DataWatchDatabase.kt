package com.ab.datawatch.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ab.datawatch.data.local.entity.DailyUsageEntity
import com.ab.datawatch.data.local.entity.TrafficStatsBaselineEntity

@Database(
    entities = [DailyUsageEntity::class, TrafficStatsBaselineEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DataWatchDatabase : RoomDatabase() {
    abstract fun usageDao(): UsageDao
}
