package com.example.app_bvc

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.d("Firebase", "Initialized: ${FirebaseApp.getApps(this).isNotEmpty()}")
    }
}