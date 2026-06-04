package com.ab.datawatch.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ab.datawatch.data.repository.NetworkStatsRepository
import com.ab.datawatch.data.repository.TrafficStatsTracker
import com.ab.datawatch.data.repository.UserPreferencesRepository
import com.ab.datawatch.util.FormatUtils
import com.ab.datawatch.worker.DailyUsageSnapshotWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NetworkMonitorService : Service() {

    @Inject lateinit var trafficStatsTracker: TrafficStatsTracker
    @Inject lateinit var networkStatsRepository: NetworkStatsRepository
    @Inject lateinit var userPreferencesRepository: UserPreferencesRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var isRunning = false
    private var isNotificationHidden = false
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        DailyUsageSnapshotWorker.schedule(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            NotificationHelper.ACTION_HIDE_NOTIF -> {
                serviceScope.launch {
                    userPreferencesRepository.setNotificationHidden(true)
                    isNotificationHidden = true
                    updateNotificationState()
                }
            }
            NotificationHelper.ACTION_STOP_SERVICE -> {
                serviceScope.launch {
                    userPreferencesRepository.setNotificationEnabled(false)
                    stopSelf()
                }
            }
            else -> {
                if (!isRunning) {
                    startTracking()
                }
            }
        }
        return START_STICKY
    }

    private fun startTracking() {
        isRunning = true
        
        serviceScope.launch {
            trafficStatsTracker.initialize()
            isNotificationHidden = userPreferencesRepository.notificationHidden.first()
            userPreferencesRepository.setServiceStartedAtLeastOnce(true)
            
            updateNotificationState()

            var dbFlushCounter = 0

            while (isActive && isRunning) {
                val isNotifEnabled = userPreferencesRepository.notificationEnabled.first()
                if (!isNotifEnabled) {
                    stopSelf()
                    break
                }

                val speedUnit = userPreferencesRepository.speedUnit.first()
                isNotificationHidden = userPreferencesRepository.notificationHidden.first()

                val speedData = trafficStatsTracker.calculateSpeed()

                if (!isNotificationHidden) {
                    // Get daily summary to update notification stats
                    // In a real app we might want to cache this and not query NetworkStatsManager every second
                    // For performance, we'll query it once every 10 seconds or rely on Flow
                    // To keep this simple and performant, we'll just mock it or query occasionally
                    val todaySummary = networkStatsRepository.getDailyUsageSummary("").first()

                    val bitmap = SpeedIconRenderer.createSpeedBitmap(
                        this@NetworkMonitorService,
                        speedData.totalSpeed,
                        speedUnit
                    )

                    val notification = notificationHelper.buildNotification(
                        rxSpeed = FormatUtils.formatSpeed(speedData.rxSpeed, speedUnit),
                        txSpeed = FormatUtils.formatSpeed(speedData.txSpeed, speedUnit),
                        mobileData = FormatUtils.formatDataSize(todaySummary.totalMobile),
                        wifiData = FormatUtils.formatDataSize(todaySummary.totalWifi),
                        speedIconBitmap = bitmap
                    )

                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
                    notificationManager.notify(NotificationHelper.NOTIFICATION_ID, notification)
                }

                dbFlushCounter++
                if (dbFlushCounter >= 30) {
                    trafficStatsTracker.flushToDb()
                    dbFlushCounter = 0
                }

                delay(1000)
            }
        }
    }
    
    private fun updateNotificationState() {
        if (isNotificationHidden) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.cancel(NotificationHelper.NOTIFICATION_ID)
            
            val minimalNotification = notificationHelper.buildMinimalNotification()
            startForeground(NotificationHelper.MINIMAL_NOTIFICATION_ID, minimalNotification)
        } else {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.cancel(NotificationHelper.MINIMAL_NOTIFICATION_ID)
            
            // Dummy initial notification until first tick
            val initialNotification = notificationHelper.buildNotification(
                "0 B/s", "0 B/s", "0 B", "0 B", null
            )
            startForeground(NotificationHelper.NOTIFICATION_ID, initialNotification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.launch {
            trafficStatsTracker.flushToDb()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
