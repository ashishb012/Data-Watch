package com.ab.datawatch.data.repository

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.RemoteException
import com.ab.datawatch.data.local.db.UsageDao
import com.ab.datawatch.data.model.AppDataUsage
import com.ab.datawatch.data.model.UsageSummary
import com.ab.datawatch.util.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageDao: UsageDao
) : NetworkStatsRepository {

    private val networkStatsManager = context.getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager

    override fun getDailyUsageSummary(date: String): Flow<UsageSummary> = flow {
        // Query NetworkStatsManager for current day's totals
        val startMillis = DateUtils.getStartOfDayEpochMillis()
        val endMillis = DateUtils.getEndOfDayEpochMillis()
        
        var mobileRx = 0L
        var mobileTx = 0L
        var wifiRx = 0L
        var wifiTx = 0L

        try {
            val mobileBucket = networkStatsManager.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,
                null, // subscriberId
                startMillis,
                endMillis
            )
            mobileRx = mobileBucket.rxBytes
            mobileTx = mobileBucket.txBytes

            val wifiBucket = networkStatsManager.querySummaryForDevice(
                ConnectivityManager.TYPE_WIFI,
                "", // subscriberId
                startMillis,
                endMillis
            )
            wifiRx = wifiBucket.rxBytes
            wifiTx = wifiBucket.txBytes
        } catch (e: SecurityException) {
            // Permission missing
        } catch (e: RemoteException) {
            // Service error
        } catch (e: Exception) {
            // Other errors
        }

        emit(UsageSummary(mobileRx, mobileTx, wifiRx, wifiTx))
    }.flowOn(Dispatchers.IO)

    override suspend fun getAppUsageForToday(): List<AppDataUsage> = withContext(Dispatchers.IO) {
        val startMillis = DateUtils.getStartOfDayEpochMillis()
        val endMillis = DateUtils.getEndOfDayEpochMillis()
        val packageManager = context.packageManager
        
        val appUsageMap = mutableMapOf<Int, AppDataUsage>()

        val processNetworkType = { networkType: Int ->
            try {
                val networkStats = networkStatsManager.querySummary(
                    networkType,
                    null,
                    startMillis,
                    endMillis
                )
                val bucket = NetworkStats.Bucket()
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucket)
                    val uid = bucket.uid
                    val rx = bucket.rxBytes
                    val tx = bucket.txBytes
                    
                    // Filter out synthetic UIDs and non-app UIDs
                    if (uid >= 10000) {
                        val existing = appUsageMap[uid]
                        if (existing != null) {
                            appUsageMap[uid] = existing.copy(
                                rxBytes = existing.rxBytes + rx,
                                txBytes = existing.txBytes + tx
                            )
                        } else {
                            val packages = packageManager.getPackagesForUid(uid)
                            if (!packages.isNullOrEmpty()) {
                                val packageName = packages[0]
                                val appInfo = try {
                                    packageManager.getApplicationInfo(packageName, 0)
                                } catch (e: Exception) { null }
                                
                                val appName = appInfo?.loadLabel(packageManager)?.toString() ?: packageName
                                val isSystemApp = appInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                                
                                appUsageMap[uid] = AppDataUsage(
                                    uid = uid,
                                    packageName = packageName,
                                    appName = appName,
                                    rxBytes = rx,
                                    txBytes = tx,
                                    isSystemApp = isSystemApp
                                )
                            }
                        }
                    }
                }
                networkStats.close()
            } catch (e: Exception) {
                // Handle exceptions
            }
        }

        processNetworkType(ConnectivityManager.TYPE_MOBILE)
        processNetworkType(ConnectivityManager.TYPE_WIFI)

        appUsageMap.values.sortedByDescending { it.totalBytes }
    }

    override suspend fun triggerDailySnapshot() = withContext(Dispatchers.IO) {
        // Handled by WorkManager primarily, but this can force a sync
        val startMillis = DateUtils.getStartOfDayEpochMillis()
        val endMillis = DateUtils.getEndOfDayEpochMillis()
        
        try {
            val mobileBucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE, null, startMillis, endMillis)
            val wifiBucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_WIFI, "", startMillis, endMillis)
            
            usageDao.upsertDailyUsage(
                com.ab.datawatch.data.local.entity.DailyUsageEntity(
                    date = DateUtils.getTodayString(),
                    mobileRxBytes = mobileBucket.rxBytes,
                    mobileTxBytes = mobileBucket.txBytes,
                    wifiRxBytes = wifiBucket.rxBytes,
                    wifiTxBytes = wifiBucket.txBytes,
                    timestamp = System.currentTimeMillis()
                )
            )
        } catch (e: Exception) {
            // Handle exceptions
        }
    }
}
