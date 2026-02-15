package com.example.screenjar.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.screenjar.data.ScreenTimeRepository
import com.example.screenjar.notification.ScreenTimeNotifier
import com.example.screenjar.service.ScreenTimeService
import com.example.screenjar.util.JarCalculator

class ScreenTimeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val service = ScreenTimeService(application)
    private val repository = ScreenTimeRepository(application)
    private val calculator = JarCalculator()
    private var notifier: ScreenTimeNotifier? = null

    val jarPercentage = MutableLiveData<Int>()
    val jarMessage = MutableLiveData<String>()

    fun initNotifications(context: Context) {
        notifier = ScreenTimeNotifier(context)
    }

    fun updateJar() {
        val usage = service.getTodayUsage()
        repository.saveScreenTime(usage)

        val percent = calculator.getFillPercentage(usage)
        val message = calculator.getMessage(percent)

        jarPercentage.value = percent
        jarMessage.value = message

        // Send notification if threshold crossed
        notifier?.sendPotionUpdateNotification(percent, message)
    }
}
