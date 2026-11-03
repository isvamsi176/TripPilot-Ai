package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val TripPilotDarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = TextPrimary,
    primaryContainer = Navy700,
    onPrimaryContainer = CyanAccent,
    secondary = RoyalViolet,
    onSecondary = TextPrimary,
    secondaryContainer = NavyCardElevated,
    onSecondaryContainer = VioletSoft,
    tertiary = CyanAccent,
    onTertiary = Navy900,
    background = Navy900,
    onBackground = TextPrimary,
    surface = Navy850,
    onSurface = TextPrimary,
    surfaceVariant = NavyCard,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    error = CrimsonAlert
)

private val TripPilotLightColorScheme = lightColorScheme(
    primary = DeepViolet,
    onPrimary = PureWhite,
    primaryContainer = SlateLight,
    onPrimaryContainer = Navy900,
    secondary = ElectricBlue,
    onSecondary = PureWhite,
    tertiary = CyanAccent,
    background = LightSurface,
    onBackground = Navy900,
    surface = PureWhite,
    onSurface = Navy900,
    surfaceVariant = SlateLight,
    onSurfaceVariant = Navy800,
    outline = BorderSubtle,
    error = CrimsonAlert
)

@Composable
fun TripPilotTheme(
    darkTheme: Boolean = true, // Default to luxury dark navy mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) TripPilotDarkColorScheme else TripPilotLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
