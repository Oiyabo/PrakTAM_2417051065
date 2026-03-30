package com.example.praktam_2417051065.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    secondary = OrangeSecondary,
    background = CreamBackground,
    surface = CardSurface,
    onPrimary = OnPrimaryText,
    onSurface = Color.Black,
    onBackground = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = OrangeSecondary,
    secondary = OrangePrimary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSurface = Color.White,
    onBackground = Color.White
)

private val GrayColorScheme = lightColorScheme(
    primary = GrayPrimary,
    secondary = GraySecondary,
    background = GrayBackground,
    surface = GraySurface,
    onPrimary = Color.White,
    onSurface = Color.Black,
    onBackground = Color.Black
)

enum class ThemeMode {
    LIGHT, DARK, GRAY
}

@Composable
fun PrakTAM_2417051065Theme(
    themeMode: ThemeMode = ThemeMode.GRAY, // Default as requested
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.GRAY -> GrayColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
