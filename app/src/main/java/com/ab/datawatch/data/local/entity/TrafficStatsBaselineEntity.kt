package com.ab.datawatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "traffic_baseline")
data class TrafficStatsBaselineEntity(
    @PrimaryKey
    val id: Int = 1,  // Singleton row

    @ColumnInfo(name = "last_total_rx_bytes")
    val lastTotalRxBytes: Long = 0L,

    @ColumnInfo(name = "last_total_tx_bytes")
    val lastTotalTxBytes: Long = 0L,

    @ColumnInfo(name = "accumulated_rx_bytes")
    val accumulatedRxBytes: Long = 0L,

    @ColumnInfo(name = "accumulated_tx_bytes")
    val accumulatedTxBytes: Long = 0L,

    @ColumnInfo(name = "last_update_timestamp")
    val lastUpdateTimestamp: Long = 0L
)
