package com.ab.datawatch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ab.datawatch.data.local.db.UsageDao
import com.ab.datawatch.data.model.AppDataUsage
import com.ab.datawatch.data.model.SpeedData
import com.ab.datawatch.data.model.SpeedUnit
import com.ab.datawatch.data.model.UsageSummary
import com.ab.datawatch.data.repository.NetworkStatsRepository
import com.ab.datawatch.data.repository.TrafficStatsTracker
import com.ab.datawatch.data.repository.UserPreferencesRepository
import com.ab.datawatch.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkStatsRepository: NetworkStatsRepository,
    private val trafficStatsTracker: TrafficStatsTracker,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val usageDao: UsageDao
) : ViewModel() {

    private val _currentSpeed = MutableStateFlow(SpeedData(0, 0, 0))
    val currentSpeed: StateFlow<SpeedData> = _currentSpeed.asStateFlow()

    private val _appUsageList = MutableStateFlow<List<AppDataUsage>>(emptyList())
    val appUsageList: StateFlow<List<AppDataUsage>> = _appUsageList.asStateFlow()

    val todaySummary = networkStatsRepository.getDailyUsageSummary(DateUtils.getTodayString())
        .stateIn(viewModelScope, SharingStarted.Lazily, UsageSummary())

    val speedUnit = userPreferencesRepository.speedUnit
        .stateIn(viewModelScope, SharingStarted.Lazily, SpeedUnit.BYTES)

    init {
        startSpeedMonitoring()
        refreshAppUsage()
    }

    private fun startSpeedMonitoring() {
        viewModelScope.launch {
            trafficStatsTracker.initialize()
            while (true) {
                _currentSpeed.value = trafficStatsTracker.calculateSpeed()
                delay(1000)
            }
        }
    }

    fun refreshAppUsage() {
        viewModelScope.launch {
            _appUsageList.value = networkStatsRepository.getAppUsageForToday()
        }
    }
}
