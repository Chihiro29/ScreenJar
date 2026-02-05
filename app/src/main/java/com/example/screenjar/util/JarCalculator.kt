package com.example.screenjar.util

class JarCalculator {

    private val dailyLimit = 4 * 60 * 60 * 1000 // 4 hours

    fun getFillPercentage(usageMillis: Long): Int {
        return ((usageMillis.toFloat() / dailyLimit) * 100)
            .toInt()
            .coerceIn(0, 100)
    }

    fun getMessage(percent: Int): String {
        return when {
            percent < 25 -> "Jar is still light 🌱"
            percent < 50 -> "Halfway full 🙂"
            percent < 75 -> "Careful, filling up 👀"
            percent < 100 -> "Almost full ⏸️"
            else -> "Jar is full! 🚫📱"
        }
    }
}
