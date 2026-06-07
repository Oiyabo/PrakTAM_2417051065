package com.example.praktam_2417051065.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.praktam_2417051065.MainViewModel
import com.example.praktam_2417051065.ui.screens.AddPage
import com.example.praktam_2417051065.ui.screens.DaftarEventScreen
import com.example.praktam_2417051065.ui.screens.LoginScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(
        navController = navController,
        startDestination = "Home"
    ) {
        composable("Home") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DaftarEventScreen(navController, viewModel)
            }
        }
        composable("addPage") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AddPage(navController, viewModel)
            }
        }
        composable("loginPage") {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                LoginScreen(navController, viewModel)
            }
        }
    }
}
