package com.example.fiend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (!AdblockEngine.isEngineLoaded) {
            android.widget.Toast.makeText(this, "Adblock not available", android.widget.Toast.LENGTH_LONG).show()
        }

        setContent {
            val playerViewModel: PlayerViewModel = viewModel()
            MainScreen(viewModel = playerViewModel)
        }
    }
}
