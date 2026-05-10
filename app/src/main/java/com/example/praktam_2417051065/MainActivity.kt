package com.example.praktam_2417051065

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
//import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.praktam_2417051065.Repository
import androidx.navigation.compose.rememberNavController
import com.example.praktam_2417051065.ui.navigation.AppNavigation
import com.example.praktam_2417051065.ui.theme.PrakTAM_2417051065Theme
import com.example.praktam_2417051065.ui.theme.ThemeMode

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrakTAM_2417051065Theme(themeMode = ThemeMode.DARK) {
                val navController = rememberNavController()
                val repo: Repository = viewModel()
                AppNavigation(navController, repo)
            }
        }
    }
}
