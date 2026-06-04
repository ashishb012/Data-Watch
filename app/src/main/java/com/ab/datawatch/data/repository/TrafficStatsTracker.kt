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
    
    // Track previous values for instantaneous speed calculation per caller to avoid delta stealing
    private val prevRxMap = mutableMapOf<String, Long>()
    private val prevTxMap = mutableMapOf<String, Long>()
    private val lastTimeMap = mutableMapOf<String, Long>()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        inMemoryBaseline = usageDao.getBaseline()
        
        val currentRx = TrafficStats.getTotalRxBytes()
        val currentTx = TrafficStats.getTotalTxBytes()
        
        if (currentRx == TrafficStats.UNSUPPORTED.toLong() || currentTx == TrafficStats.UNSUPPORTED.toLong()) {
            return@withContext
        }
        
        // Initialize base states if needed, but per-caller logic handles it gracefully
        // We still need to record the absolute baseline for db storage
        
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

    fun calculateSpeed(callerId: String): SpeedData {
        val currentRx = TrafficStats.getTotalRxBytes()
        val currentTx = TrafficStats.getTotalTxBytes()
        val currentTime = System.currentTimeMillis()
        
        if (currentRx == TrafficStats.UNSUPPORTED.toLong() || currentTx == TrafficStats.UNSUPPORTED.toLong()) {
            return SpeedData(0, 0, 0)
        }
        
        val prevRx = prevRxMap[callerId] ?: -1L
        val prevTx = prevTxMap[callerId] ?: -1L
        val lastTime = lastTimeMap[callerId] ?: -1L
        
        if (prevRx == -1L || prevTx == -1L || lastTime == -1L) {
            prevRxMap[callerId] = currentRx
            prevTxMap[callerId] = currentTx
            lastTimeMap[callerId] = currentTime
            return SpeedData(0, 0, 0)
        }
        
        val timeDiff = currentTime - lastTime
        if (timeDiff <= 0) return SpeedData(0, 0, 0)
        
        var rxDiff = currentRx - prevRx
        var txDiff = currentTx - prevTx
        
        // Handle reboot during tracking
        if (rxDiff < 0) rxDiff = currentRx
        if (txDiff < 0) txDiff = currentTx
        
        val rxSpeed = (rxDiff * 1000) / timeDiff
        val txSpeed = (txDiff * 1000) / timeDiff
        
        prevRxMap[callerId] = currentRx
        prevTxMap[callerId] = currentTx
        lastTimeMap[callerId] = currentTime
        
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
