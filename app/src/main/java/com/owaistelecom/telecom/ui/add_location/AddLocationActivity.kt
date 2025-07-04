package com.owaistelecom.telecom.ui.add_location

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddLocationActivity : ComponentActivity() {
    val myLocationManager:MyLocationManager = MyLocationManager(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddLocationScreen(myLocationManager)
        }
    }
}




