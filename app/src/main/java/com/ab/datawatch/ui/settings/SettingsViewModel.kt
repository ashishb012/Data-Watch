package com.ab.datawatch.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.data.model.ThemeColor
import com.ab.datawatch.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val notificationEnabled = userPreferencesRepository.notificationEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val speedUnit = userPreferencesRepository.speedUnit
        .stateIn(viewModelScope, SharingStarted.Lazily, SpeedUnit.BYTES)

    val themeColor = userPreferencesRepository.themeColor
        .stateIn(viewModelScope, SharingStarted.Lazily, ThemeColor.BLUE)

    val isDarkMode = userPreferencesRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun toggleNotification(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setNotificationEnabled(enabled)
            if (enabled) {
                userPreferencesRepository.setNotificationHidden(false)
            }
        }
    }

    fun setSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch {
            userPreferencesRepository.setSpeedUnit(unit)
        }
    }

    fun setThemeColor(color: ThemeColor) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeColor(color)
        }
    }

    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setIsDarkMode(isDark)
        }
    }
}
