package com.example.praktam_2417051065.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = WarmOrangePrimary,
    onPrimary = WarmOrangeOnPrimary,
    primaryContainer = WarmOrangePrimaryContainer,
    onPrimaryContainer = WarmOrangeOnPrimaryContainer,
    secondary = WarmOrangeSecondary,
    onSecondary = WarmOrangeOnSecondary,
    secondaryContainer = WarmOrangeSecondaryContainer,
    onSecondaryContainer = WarmOrangeOnSecondaryContainer,
    tertiary = WarmOrangeTertiary,
    onTertiary = WarmOrangeOnTertiary,
    tertiaryContainer = WarmOrangeTertiaryContainer,
    onTertiaryContainer = WarmOrangeOnTertiaryContainer,
    background = WarmCreamBackground,
    surface = WarmCreamSurface,
    onSurface = WarmOnSurface,
    onBackground = WarmOnSurface,
    onSurfaceVariant = WarmOnSurfaceVariant,
    surfaceVariant = Color(0xFFF5EFEA) // Slightly darker than background for cards
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkWarmPrimary,
    onPrimary = DarkWarmOnPrimary,
    primaryContainer = DarkWarmPrimaryContainer,
    onPrimaryContainer = DarkWarmOnPrimaryContainer,
    secondary = DarkWarmSecondary,
    onSecondary = DarkWarmOnSecondary,
    secondaryContainer = DarkWarmSecondaryContainer,
    onSecondaryContainer = DarkWarmOnSecondaryContainer,
    tertiary = DarkWarmTertiary,
    onTertiary = DarkWarmOnTertiary,
    background = DarkWarmBackground,
    surface = DarkWarmSurface,
    onSurface = DarkWarmOnSurface,
    onBackground = DarkWarmOnSurface,
    onSurfaceVariant = DarkWarmOnSurfaceVariant,
    surfaceVariant = Color(0xFF33302E)
)

enum class ThemeMode {
    LIGHT, DARK
}

@Composable
fun PrakTAM_2417051065Theme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
