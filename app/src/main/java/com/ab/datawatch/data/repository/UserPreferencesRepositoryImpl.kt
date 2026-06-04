package com.ab.datawatch.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.ab.datawatch.data.local.datastore.PreferenceKeys
import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.data.model.ThemeColor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private val dataStore = context.dataStore

    override val notificationEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.NOTIFICATION_ENABLED] ?: true }

    override val speedUnit: Flow<SpeedUnit> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val unitString = preferences[PreferenceKeys.SPEED_UNIT] ?: SpeedUnit.BYTES.name
            try {
                SpeedUnit.valueOf(unitString)
            } catch (e: IllegalArgumentException) {
                SpeedUnit.BYTES
            }
        }

    override val themeColor: Flow<ThemeColor> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val colorString = preferences[PreferenceKeys.THEME_COLOR] ?: ThemeColor.BLUE.name
            try {
                ThemeColor.valueOf(colorString)
            } catch (e: IllegalArgumentException) {
                ThemeColor.BLUE
            }
        }

    override val isDarkMode: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.IS_DARK_MODE] ?: true }

    override val mobileDataLimitMb: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.MOBILE_DATA_LIMIT_MB] ?: 0L }

    override val wifiDataLimitMb: Flow<Long> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.WIFI_DATA_LIMIT_MB] ?: 0L }

    override val hasShownBatteryOpt: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.HAS_SHOWN_BATTERY_OPT] ?: false }

    override val serviceStartedAtLeastOnce: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.SERVICE_STARTED_AT_LEAST_ONCE] ?: false }

    override val notificationHidden: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.NOTIFICATION_HIDDEN] ?: false }

    override val dataLimitWarnedMobile: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.DATA_LIMIT_WARNED_MOBILE] ?: "" }

    override val dataLimitWarnedWifi: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { it[PreferenceKeys.DATA_LIMIT_WARNED_WIFI] ?: "" }

    override suspend fun setNotificationEnabled(enabled: Boolean) {
        dataStore.edit { it[PreferenceKeys.NOTIFICATION_ENABLED] = enabled }
    }

    override suspend fun setSpeedUnit(unit: SpeedUnit) {
        dataStore.edit { it[PreferenceKeys.SPEED_UNIT] = unit.name }
    }

    override suspend fun setThemeColor(color: ThemeColor) {
        dataStore.edit { it[PreferenceKeys.THEME_COLOR] = color.name }
    }

    override suspend fun setIsDarkMode(isDark: Boolean) {
        dataStore.edit { it[PreferenceKeys.IS_DARK_MODE] = isDark }
    }

    override suspend fun setMobileDataLimitMb(limit: Long) {
        dataStore.edit { it[PreferenceKeys.MOBILE_DATA_LIMIT_MB] = limit }
    }

    override suspend fun setWifiDataLimitMb(limit: Long) {
        dataStore.edit { it[PreferenceKeys.WIFI_DATA_LIMIT_MB] = limit }
    }

    override suspend fun setHasShownBatteryOpt(shown: Boolean) {
        dataStore.edit { it[PreferenceKeys.HAS_SHOWN_BATTERY_OPT] = shown }
    }

    override suspend fun setServiceStartedAtLeastOnce(started: Boolean) {
        dataStore.edit { it[PreferenceKeys.SERVICE_STARTED_AT_LEAST_ONCE] = started }
    }

    override suspend fun setNotificationHidden(hidden: Boolean) {
        dataStore.edit { it[PreferenceKeys.NOTIFICATION_HIDDEN] = hidden }
    }

    override suspend fun setDataLimitWarnedMobile(month: String) {
        dataStore.edit { it[PreferenceKeys.DATA_LIMIT_WARNED_MOBILE] = month }
    }

    override suspend fun setDataLimitWarnedWifi(month: String) {
        dataStore.edit { it[PreferenceKeys.DATA_LIMIT_WARNED_WIFI] = month }
    }
}
