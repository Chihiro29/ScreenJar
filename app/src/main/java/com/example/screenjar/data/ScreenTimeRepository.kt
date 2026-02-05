package com.example.screenjar.data

import android.content.Context

class ScreenTimeRepository(context: Context) {

    private val prefs =
        context.getSharedPreferences("screenjar_prefs", Context.MODE_PRIVATE)

    fun saveScreenTime(millis: Long) {
        prefs.edit().putLong("screen_time_today", millis).apply()
    }

    fun getScreenTime(): Long {
        return prefs.getLong("screen_time_today", 0L)
    }
}
