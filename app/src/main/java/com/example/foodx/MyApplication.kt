package com.example.foodx

import android.app.Application
import timber.log.Timber

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
    }
}