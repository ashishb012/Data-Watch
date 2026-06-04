package com.ab.datawatch.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_usage")
data class DailyUsageEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,  // Format: "yyyy-MM-dd" (ISO 8601 date)

    @ColumnInfo(name = "mobile_rx_bytes", defaultValue = "0")
    val mobileRxBytes: Long = 0L,

    @ColumnInfo(name = "mobile_tx_bytes", defaultValue = "0")
    val mobileTxBytes: Long = 0L,

    @ColumnInfo(name = "wifi_rx_bytes", defaultValue = "0")
    val wifiRxBytes: Long = 0L,

    @ColumnInfo(name = "wifi_tx_bytes", defaultValue = "0")
    val wifiTxBytes: Long = 0L,

    @ColumnInfo(name = "timestamp", defaultValue = "0")
    val timestamp: Long = 0L  // Epoch millis when the row was written
)
