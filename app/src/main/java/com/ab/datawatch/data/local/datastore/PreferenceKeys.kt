package com.ab.datawatch.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
    val SPEED_UNIT = stringPreferencesKey("speed_unit")          // "BYTES" or "BITS"
    val THEME_COLOR = stringPreferencesKey("theme_color")        // "BLUE", "ORANGE", "GREEN", "PURPLE"
    val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")     // defaults true
    val MOBILE_DATA_LIMIT_MB = longPreferencesKey("mobile_data_limit_mb")  // 0 = disabled
    val WIFI_DATA_LIMIT_MB = longPreferencesKey("wifi_data_limit_mb")      // 0 = disabled
    val HAS_SHOWN_BATTERY_OPT = booleanPreferencesKey("has_shown_battery_opt")
    val SERVICE_STARTED_AT_LEAST_ONCE = booleanPreferencesKey("service_started")
    val NOTIFICATION_HIDDEN = booleanPreferencesKey("notification_hidden")  // true = notif hidden, service still runs
    val DATA_LIMIT_WARNED_MOBILE = stringPreferencesKey("data_limit_warned_mobile")  // "yyyy-MM" of last warning
    val DATA_LIMIT_WARNED_WIFI = stringPreferencesKey("data_limit_warned_wifi")      // "yyyy-MM" of last warning
}
