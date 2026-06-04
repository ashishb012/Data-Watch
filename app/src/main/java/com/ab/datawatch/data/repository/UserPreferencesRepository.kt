package com.ab.datawatch.data.repository

import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.data.model.ThemeColor
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val notificationEnabled: Flow<Boolean>
    val speedUnit: Flow<SpeedUnit>
    val themeColor: Flow<ThemeColor>
    val isDarkMode: Flow<Boolean>
    val mobileDataLimitMb: Flow<Long>
    val wifiDataLimitMb: Flow<Long>
    val hasShownBatteryOpt: Flow<Boolean>
    val serviceStartedAtLeastOnce: Flow<Boolean>
    val notificationHidden: Flow<Boolean>
    val dataLimitWarnedMobile: Flow<String>
    val dataLimitWarnedWifi: Flow<String>

    suspend fun setNotificationEnabled(enabled: Boolean)
    suspend fun setSpeedUnit(unit: SpeedUnit)
    suspend fun setThemeColor(color: ThemeColor)
    suspend fun setIsDarkMode(isDark: Boolean)
    suspend fun setMobileDataLimitMb(limit: Long)
    suspend fun setWifiDataLimitMb(limit: Long)
    suspend fun setHasShownBatteryOpt(shown: Boolean)
    suspend fun setServiceStartedAtLeastOnce(started: Boolean)
    suspend fun setNotificationHidden(hidden: Boolean)
    suspend fun setDataLimitWarnedMobile(month: String)
    suspend fun setDataLimitWarnedWifi(month: String)
}
