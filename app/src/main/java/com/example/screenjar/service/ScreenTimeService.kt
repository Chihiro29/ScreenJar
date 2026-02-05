package com.example.screenjar.service

import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.*

class ScreenTimeService(private val context: Context) {

    fun getTodayUsage(): Long {
        val manager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startTime = calendar.timeInMillis

        val stats = manager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        var total = 0L
        stats?.forEach { total += it.totalTimeInForeground }

        return total
    }
}
