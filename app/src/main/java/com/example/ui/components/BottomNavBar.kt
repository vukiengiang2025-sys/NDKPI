package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String, val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard)
    object Yearly : Screen("yearly", "Năm", Icons.Filled.DateRange, Icons.Outlined.DateRange)
    object Income : Screen("income", "Thu nhập", Icons.Filled.MonetizationOn, Icons.Outlined.MonetizationOn)
    object AiCoach : Screen("aicoach", "AI Coach", Icons.Filled.Psychology, Icons.Outlined.Psychology)
    object Settings : Screen("settings", "Cài đặt", Icons.Filled.Settings, Icons.Outlined.Settings)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Yearly,
    Screen.Income,
    Screen.AiCoach,
    Screen.Settings
)

@Composable
fun MainBottomNavBar(navController: NavController) {
    NavigationBar(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon, 
                        contentDescription = screen.title 
                    ) 
                },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
