package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.MainViewModel
import com.example.ui.components.MainBottomNavBar
import com.example.ui.components.Screen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IncomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.YearlyScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { MainBottomNavBar(navController = navController) },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    NavHost(
                        navController = navController, 
                        startDestination = Screen.Dashboard.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Screen.Yearly.route) {
                            YearlyScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Screen.Income.route) {
                            IncomeScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Screen.AiCoach.route) {
                            AiCoachScreen(viewModel = viewModel, navController = navController)
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen(viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
