package com.example.screenjar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.screenjar.data.ScreenTimeRepository
import com.example.screenjar.service.ScreenTimeService
import com.example.screenjar.util.JarCalculator

class ScreenTimeViewModel(application: Application) :
    AndroidViewModel(application) {

    private val service = ScreenTimeService(application)
    private val repository = ScreenTimeRepository(application)
    private val calculator = JarCalculator()

    val jarPercentage = MutableLiveData<Int>()
    val jarMessage = MutableLiveData<String>()

    fun updateJar() {
        val usage = service.getTodayUsage()
        repository.saveScreenTime(usage)

        val percent = calculator.getFillPercentage(usage)
        jarPercentage.value = percent
        jarMessage.value = calculator.getMessage(percent)
    }
}
