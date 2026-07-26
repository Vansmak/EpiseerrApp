package com.episeerr.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.content.Context

@Composable
fun EpiseerrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Defaults to the brand palette rather than Android 12+ wallpaper-derived colors, so the
    // app reads as the same product as the Episeerr web dashboard instead of a generic
    // Material You app that happens to match whatever wallpaper the user has set.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context: Context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> EpiseerrDarkColorScheme
        else -> EpiseerrLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EpiseerrTypography,
        shapes = EpiseerrShapes,
        content = content
    )
}
