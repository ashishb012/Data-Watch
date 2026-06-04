package com.ab.datawatch.data.repository

import com.ab.datawatch.data.model.AppDataUsage
import com.ab.datawatch.data.model.UsageSummary
import kotlinx.coroutines.flow.Flow

interface NetworkStatsRepository {
    fun getDailyUsageSummary(date: String): Flow<UsageSummary>
    suspend fun getAppUsageForToday(): List<AppDataUsage>
    suspend fun triggerDailySnapshot()
}
