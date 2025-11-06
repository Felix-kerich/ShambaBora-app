package com.app.shamba_bora

import android.app.Application
import com.app.shamba_bora.utils.PreferenceManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShambaBoraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferenceManager.init(this)
    }
}

