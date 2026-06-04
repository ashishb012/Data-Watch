package com.ab.datawatch.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.ab.datawatch.MainActivity
import com.ab.datawatch.R

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "datawatch_speed_channel"
        const val MINIMAL_CHANNEL_ID = "datawatch_minimal_channel"
        const val NOTIFICATION_ID = 1001
        const val MINIMAL_NOTIFICATION_ID = 1002

        const val ACTION_HIDE_NOTIF = "com.ab.datawatch.ACTION_HIDE_NOTIF"
        const val ACTION_STOP_SERVICE = "com.ab.datawatch.ACTION_STOP_SERVICE"
    }

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Speed Monitor"
            val descriptionText = "Displays real-time network speed in the status bar"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(false)
            }
            
            val minimalName = "Background Service"
            val minimalDesc = "Keeps the background tracking service alive"
            val minimalImportance = NotificationManager.IMPORTANCE_MIN
            val minimalChannel = NotificationChannel(MINIMAL_CHANNEL_ID, minimalName, minimalImportance).apply {
                description = minimalDesc
                setShowBadge(false)
            }

            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(minimalChannel)
        }
    }

    fun buildNotification(
        rxSpeed: String,
        txSpeed: String,
        mobileData: String,
        wifiData: String,
        speedIconBitmap: android.graphics.Bitmap?
    ): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentTitle("↓ $rxSpeed     ↑ $txSpeed")
            .setContentText("📱 Mobile: $mobileData   📶 WiFi: $wifiData")
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)

        if (speedIconBitmap != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSmallIcon(androidx.core.graphics.drawable.IconCompat.createWithBitmap(speedIconBitmap))
        } else {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        }

        return builder.build()
    }

    fun buildMinimalNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, MINIMAL_CHANNEL_ID)
            .setContentTitle("DataWatch is running")
            .setContentText("Network tracking is active in the background")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
            .build()
    }
}
