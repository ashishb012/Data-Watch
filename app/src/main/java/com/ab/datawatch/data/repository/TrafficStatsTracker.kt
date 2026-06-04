package com.ab.datawatch.data.repository

import android.net.TrafficStats
import com.ab.datawatch.data.local.db.UsageDao
import com.ab.datawatch.data.local.entity.TrafficStatsBaselineEntity
import com.ab.datawatch.data.model.SpeedData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrafficStatsTracker @Inject constructor(
    private val usageDao: UsageDao
) {
    private var inMemoryBaseline: TrafficStatsBaselineEntity? = null
    
    // Track previous values for instantaneous speed calculation
    private var prevRxForSpeed: Long = -1
    private var prevTxForSpeed: Long = -1
    private var lastSpeedCheckTime: Long = -1

    suspend fun initialize() = withContext(Dispatchers.IO) {
        inMemoryBaseline = usageDao.getBaseline()
        
        val currentRx = TrafficStats.getTotalRxBytes()
        val currentTx = TrafficStats.getTotalTxBytes()
        
        if (currentRx == TrafficStats.UNSUPPORTED.toLong() || currentTx == TrafficStats.UNSUPPORTED.toLong()) {
            return@withContext
        }
        
        prevRxForSpeed = currentRx
        prevTxForSpeed = currentTx
        lastSpeedCheckTime = System.currentTimeMillis()
        
        if (inMemoryBaseline == null || currentRx < inMemoryBaseline!!.lastTotalRxBytes || currentTx < inMemoryBaseline!!.lastTotalTxBytes) {
            // Device likely rebooted, or first run
            val accumulatedRx = inMemoryBaseline?.accumulatedRxBytes ?: 0L
            val accumulatedTx = inMemoryBaseline?.accumulatedTxBytes ?: 0L
            
            inMemoryBaseline = TrafficStatsBaselineEntity(
                id = 1,
                lastTotalRxBytes = currentRx,
                lastTotalTxBytes = currentTx,
                accumulatedRxBytes = accumulatedRx,
                accumulatedTxBytes = accumulatedTx,
                lastUpdateTimestamp = System.currentTimeMillis()
            )
            flushToDb()
        }
    }

    fun calculateSpeed(): SpeedData {
        val currentRx = TrafficStats.getTotalRxBytes()
        val currentTx = TrafficStats.getTotalTxBytes()
        val currentTime = System.currentTimeMillis()
        
        if (currentRx == TrafficStats.UNSUPPORTED.toLong() || currentTx == TrafficStats.UNSUPPORTED.toLong()) {
            return SpeedData(0, 0, 0)
        }
        
        if (prevRxForSpeed == -1L || prevTxForSpeed == -1L || lastSpeedCheckTime == -1L) {
            prevRxForSpeed = currentRx
            prevTxForSpeed = currentTx
            lastSpeedCheckTime = currentTime
            return SpeedData(0, 0, 0)
        }
        
        val timeDiff = currentTime - lastSpeedCheckTime
        if (timeDiff <= 0) return SpeedData(0, 0, 0)
        
        var rxDiff = currentRx - prevRxForSpeed
        var txDiff = currentTx - prevTxForSpeed
        
        // Handle reboot during tracking
        if (rxDiff < 0) rxDiff = currentRx
        if (txDiff < 0) txDiff = currentTx
        
        val rxSpeed = (rxDiff * 1000) / timeDiff
        val txSpeed = (txDiff * 1000) / timeDiff
        
        prevRxForSpeed = currentRx
        prevTxForSpeed = currentTx
        lastSpeedCheckTime = currentTime
        
        // Update baseline tracking
        inMemoryBaseline?.let { baseline ->
            if (currentRx < baseline.lastTotalRxBytes || currentTx < baseline.lastTotalTxBytes) {
                // Reboot detected since last check
                inMemoryBaseline = baseline.copy(
                    lastTotalRxBytes = currentRx,
                    lastTotalTxBytes = currentTx,
                    accumulatedRxBytes = baseline.accumulatedRxBytes + baseline.lastTotalRxBytes,
                    accumulatedTxBytes = baseline.accumulatedTxBytes + baseline.lastTotalTxBytes,
                    lastUpdateTimestamp = currentTime
                )
            } else {
                inMemoryBaseline = baseline.copy(
                    lastTotalRxBytes = currentRx,
                    lastTotalTxBytes = currentTx,
                    lastUpdateTimestamp = currentTime
                )
            }
        }
        
        return SpeedData(
            rxSpeed = rxSpeed,
            txSpeed = txSpeed,
            totalSpeed = rxSpeed + txSpeed
        )
    }

    suspend fun flushToDb() = withContext(Dispatchers.IO) {
        inMemoryBaseline?.let {
            usageDao.upsertBaseline(it)
        }
    }
}
