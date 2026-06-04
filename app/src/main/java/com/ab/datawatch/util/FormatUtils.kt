package com.ab.datawatch.util

import com.ab.datawatch.data.model.SpeedUnit
import java.util.Locale

object FormatUtils {
    // 1000-based for speeds, 1024-based for data totals
    private const val KILO_BYTES = 1000L
    private const val MEGA_BYTES = KILO_BYTES * 1000L
    private const val GIGA_BYTES = MEGA_BYTES * 1000L

    private const val KIBI_BYTES = 1024L
    private const val MEBI_BYTES = KIBI_BYTES * 1024L
    private const val GIBI_BYTES = MEBI_BYTES * 1024L

    fun formatSpeed(bytesPerSec: Long, unit: SpeedUnit): String {
        val value = if (unit == SpeedUnit.BITS) bytesPerSec * 8 else bytesPerSec
        val suffixMultiplier = if (unit == SpeedUnit.BITS) "bps" else "B/s"
        
        return when {
            value >= GIGA_BYTES -> String.format(Locale.getDefault(), "%.1f G%s", value / GIGA_BYTES.toDouble(), suffixMultiplier)
            value >= MEGA_BYTES -> String.format(Locale.getDefault(), "%.1f M%s", value / MEGA_BYTES.toDouble(), suffixMultiplier)
            else -> String.format(Locale.getDefault(), "%.1f K%s", value / KILO_BYTES.toDouble(), suffixMultiplier)
        }
    }

    fun formatDataSize(bytes: Long): String {
        return when {
            bytes >= GIBI_BYTES -> String.format(Locale.getDefault(), "%.2f GB", bytes / GIBI_BYTES.toDouble())
            bytes >= MEBI_BYTES -> String.format(Locale.getDefault(), "%.1f MB", bytes / MEBI_BYTES.toDouble())
            bytes >= KIBI_BYTES -> String.format(Locale.getDefault(), "%.0f KB", bytes / KIBI_BYTES.toDouble())
            else -> "$bytes B"
        }
    }
}
