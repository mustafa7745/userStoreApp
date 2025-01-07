package com.owaistelecom.telecom.application
import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        AppContext = this
    }
    companion object {
        lateinit var AppContext: Application
            private set

    }

}