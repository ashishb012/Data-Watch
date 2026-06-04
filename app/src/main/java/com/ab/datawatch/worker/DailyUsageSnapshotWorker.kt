package com.ab.datawatch.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.ab.datawatch.data.local.db.UsageDao
import com.ab.datawatch.data.local.entity.DailyUsageEntity
import com.ab.datawatch.data.repository.NetworkStatsRepository
import com.ab.datawatch.util.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyUsageSnapshotWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val networkStatsRepository: NetworkStatsRepository,
    private val usageDao: UsageDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val today = DateUtils.getTodayString()
            val stats = networkStatsRepository.getDailyUsageSummary(today).first()
            
            usageDao.upsertDailyUsage(
                DailyUsageEntity(
                    date = today,
                    mobileRxBytes = stats.mobileRxBytes,
                    mobileTxBytes = stats.mobileTxBytes,
                    wifiRxBytes = stats.wifiRxBytes,
                    wifiTxBytes = stats.wifiTxBytes,
                    timestamp = System.currentTimeMillis()
                )
            )
            
            // Cleanup: delete rows older than 90 days
            val cutoff = LocalDate.now().minusDays(90).toString()
            usageDao.deleteOlderThan(cutoff)
            
            Result.success()
        } catch (e: SecurityException) {
            // PACKAGE_USAGE_STATS permission revoked
            Result.failure()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .setRequiresDeviceIdle(false)
                .setRequiresStorageNotLow(true)
                .build()

            val snapshotRequest = PeriodicWorkRequestBuilder<DailyUsageSnapshotWorker>(
                repeatInterval = 24, 
                repeatIntervalTimeUnit = TimeUnit.HOURS,
                flexInterval = 2,
                flexTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag("daily_usage_snapshot")
                .setInitialDelay(calculateDelayUntil2300(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "daily_usage_snapshot",
                ExistingPeriodicWorkPolicy.KEEP,
                snapshotRequest
            )
        }

        private fun calculateDelayUntil2300(): Long {
            val now = LocalDateTime.now()
            var target = now.withHour(23).withMinute(0).withSecond(0)
            
            if (now.isAfter(target)) {
                target = target.plusDays(1)
            }
            
            return ChronoUnit.MILLIS.between(now, target)
        }
    }
}
