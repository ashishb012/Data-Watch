package com.ab.datawatch.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ab.datawatch.data.local.entity.DailyUsageEntity
import com.ab.datawatch.data.local.entity.TrafficStatsBaselineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {

    // --- Daily Usage ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyUsage(usage: DailyUsageEntity)

    @Query("SELECT * FROM daily_usage WHERE date = :date LIMIT 1")
    suspend fun getUsageForDate(date: String): DailyUsageEntity?

    @Query("SELECT * FROM daily_usage WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getUsageRange(startDate: String, endDate: String): Flow<List<DailyUsageEntity>>

    @Query("SELECT * FROM daily_usage WHERE date >= :startDate ORDER BY date ASC")
    fun getUsageSince(startDate: String): Flow<List<DailyUsageEntity>>

    @Query("DELETE FROM daily_usage WHERE date < :cutoffDate")
    suspend fun deleteOlderThan(cutoffDate: String)

    @Query("SELECT COUNT(*) FROM daily_usage")
    suspend fun getRowCount(): Int

    // --- Traffic Baseline ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBaseline(baseline: TrafficStatsBaselineEntity)

    @Query("SELECT * FROM traffic_baseline WHERE id = 1 LIMIT 1")
    suspend fun getBaseline(): TrafficStatsBaselineEntity?
}
