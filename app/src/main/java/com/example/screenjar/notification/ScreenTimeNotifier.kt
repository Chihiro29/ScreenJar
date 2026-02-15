package com.example.screenjar.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.screenjar.MainActivity

class ScreenTimeNotifier(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "screen_jar_channel"
        private const val NOTIFICATION_ID = 1001
        private const val PREFS_NAME = "screenjar_notifications"
        private const val KEY_LAST_NOTIFIED_PERCENT = "last_notified_percent"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ScreenJar Updates"
            val descriptionText = "Notifications about your screen time and potion status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendPotionUpdateNotification(percent: Int, message: String) {
        // Check permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // Only notify if we crossed a threshold
        if (!shouldNotify(percent)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val potionState = getPotionState(percent)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("ScreenJar Update: $potionState")
            .setContentText("Screen time impact: $percent% - $message")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)

        // Save the notified percent
        prefs.edit().putInt(KEY_LAST_NOTIFIED_PERCENT, percent).apply()
    }

    private fun shouldNotify(currentPercent: Int): Boolean {
        val lastNotified = prefs.getInt(KEY_LAST_NOTIFIED_PERCENT, -1)
        if (lastNotified == -1) return true // First time

        // Notify when crossing thresholds: 10, 30, 55, 75, 90
        val thresholds = listOf(10, 30, 55, 75, 90)
        return thresholds.any { threshold ->
            (lastNotified < threshold && currentPercent >= threshold) ||
            (lastNotified >= threshold && currentPercent < threshold)
        }
    }

    private fun getPotionState(percent: Int): String {
        return when {
            percent <= 10 -> "Pure Potion ✨"
            percent <= 30 -> "Nearly Empty 🫗"
            percent <= 55 -> "Getting Bad ⚠️"
            percent <= 75 -> "Okay Level 📊"
            percent <= 90 -> "Worse Brew 🧪"
            else -> "Dangerously Potent! 💀"
        }
    }

    fun notifyLimitReached() {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("ScreenJar")
            .setContentText("You exceeded your daily screen time limit.")
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }
}
