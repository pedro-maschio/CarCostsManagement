package com.pedro.maschio.carcostsmanagement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pedro.maschio.carcostsmanagement.ui.NavHost
import com.pedro.maschio.carcostsmanagement.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                NavHost()
            }
        }
    }
}
