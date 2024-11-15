package com.huseyinkiran.trendavmapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.perf.metrics.AddTrace

class UygunApp : Application() {
    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate() {
        FirebaseApp.initializeApp(this@UygunApp)
        super.onCreate()
    }
}