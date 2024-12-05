package com.example.cryptosignal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptosignal.presentation.ui.HomeScreen
import com.example.cryptosignal.presentation.viewmodel.MainViewModel
import com.example.cryptosignal.ui.theme.CryptoSignalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        super.onCreate(savedInstanceState)
        setContent {
            CryptoSignalTheme {
                val viewModel: MainViewModel = viewModel()
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}