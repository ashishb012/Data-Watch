package com.ab.datawatch.data.model

enum class SpeedUnit {
    BYTES, BITS
}

enum class ThemeColor {
    BLUE, ORANGE, GREEN, PURPLE
}

data class SpeedData(
    val rxSpeed: Long,
    val txSpeed: Long,
    val totalSpeed: Long
)

data class UsageSummary(
    val mobileRxBytes: Long = 0L,
    val mobileTxBytes: Long = 0L,
    val wifiRxBytes: Long = 0L,
    val wifiTxBytes: Long = 0L
) {
    val totalMobile: Long get() = mobileRxBytes + mobileTxBytes
    val totalWifi: Long get() = wifiRxBytes + wifiTxBytes
    val total: Long get() = totalMobile + totalWifi
}

data class AppDataUsage(
    val uid: Int,
    val packageName: String,
    val appName: String,
    val rxBytes: Long,
    val txBytes: Long,
    val isSystemApp: Boolean
) {
    val totalBytes: Long get() = rxBytes + txBytes
}
