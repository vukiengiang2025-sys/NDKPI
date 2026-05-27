package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val NamDuocColorScheme = darkColorScheme(
    primary = Blue500,
    secondary = Purple500,
    tertiary = Emerald500,
    background = Slate950,
    surface = Slate900,
    surfaceVariant = Slate800,
    onPrimary = Slate950,
    onSecondary = Slate950,
    onTertiary = Slate950,
    onBackground = Slate200,
    onSurface = Slate200,
    onSurfaceVariant = Slate400,
    error = Red500
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NamDuocColorScheme,
        typography = Typography,
        content = content
    )
}
