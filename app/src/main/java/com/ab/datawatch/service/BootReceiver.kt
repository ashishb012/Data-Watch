package com.ab.datawatch.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ab.datawatch.data.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    private val goAsyncScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
            intent.action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            
            goAsyncScope.launch {
                try {
                    val isNotificationEnabled = userPreferencesRepository.notificationEnabled.first()
                    val hasStartedOnce = userPreferencesRepository.serviceStartedAtLeastOnce.first()
                    
                    if (isNotificationEnabled && hasStartedOnce) {
                        val serviceIntent = Intent(context, NetworkMonitorService::class.java)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(serviceIntent)
                        } else {
                            context.startService(serviceIntent)
                        }
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
