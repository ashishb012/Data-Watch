package com.ab.datawatch.ui.appdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ab.datawatch.data.model.AppDataUsage
import com.ab.datawatch.data.repository.NetworkStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppDetailViewModel @Inject constructor(
    private val networkStatsRepository: NetworkStatsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val uid: Int = savedStateHandle.get<Int>("uid") ?: -1

    private val _appData = MutableStateFlow<AppDataUsage?>(null)
    val appData: StateFlow<AppDataUsage?> = _appData.asStateFlow()

    init {
        loadAppDetails()
    }

    private fun loadAppDetails() {
        viewModelScope.launch {
            val allApps = networkStatsRepository.getAppUsageForToday()
            _appData.value = allApps.find { it.uid == uid }
        }
    }
}
