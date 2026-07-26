package com.episeerr.app.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Brand palette - matches the Episeerr web dashboard's default (indigo/violet) theme
// so the companion app reads as the same product rather than a generic Material app.
val Indigo80 = Color(0xFFC2C4FF)
val Indigo60 = Color(0xFF8B8DFF)
val Indigo40 = Color(0xFF6366F1)
val Indigo20 = Color(0xFF32339A)

val Violet80 = Color(0xFFDCC8FF)
val Violet40 = Color(0xFF8B5CF6)

val Emerald40 = Color(0xFF34D399)
val Emerald20 = Color(0xFF0F5B41)
val Amber40 = Color(0xFFFBBF24)
val Red80 = Color(0xFFFFB4AB)
val Red40 = Color(0xFFF87171)
val Red20 = Color(0xFF8C1D18)

val Slate99 = Color(0xFFFBFAFF)
val Slate95 = Color(0xFFEFEFF7)
val Slate90 = Color(0xFFE2E1EC)
val Slate10 = Color(0xFF0F1419)
val Slate12 = Color(0xFF1A1F24)
val Slate17 = Color(0xFF242A32)
val Slate24 = Color(0xFF2E353E)

val StatusConnected = Emerald40
val StatusDisabled = Amber40
val StatusUnconfigured = Color(0xFF9CA3AF)

val EpiseerrDarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Indigo20,
    primaryContainer = Indigo20,
    onPrimaryContainer = Indigo80,
    secondary = Violet80,
    onSecondary = Color(0xFF3F1E80),
    secondaryContainer = Color(0xFF4F2E97),
    onSecondaryContainer = Violet80,
    tertiary = Emerald40,
    onTertiary = Emerald20,
    background = Slate10,
    onBackground = Color(0xFFF5F5F7),
    surface = Slate12,
    onSurface = Color(0xFFF5F5F7),
    surfaceVariant = Slate17,
    onSurfaceVariant = Color(0xFFA0A5AD),
    surfaceContainer = Slate17,
    surfaceContainerHigh = Slate24,
    surfaceContainerLow = Slate12,
    outline = Slate24,
    error = Red80,
    onError = Red20,
    errorContainer = Red20,
    onErrorContainer = Red80,
)

val EpiseerrLightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E0FF),
    onPrimaryContainer = Indigo20,
    secondary = Violet40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE1FF),
    onSecondaryContainer = Color(0xFF2A1158),
    tertiary = Color(0xFF0B815A),
    onTertiary = Color.White,
    background = Slate99,
    onBackground = Color(0xFF1A1C1E),
    surface = Slate99,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Slate95,
    onSurfaceVariant = Color(0xFF45464F),
    surfaceContainer = Slate95,
    surfaceContainerHigh = Slate90,
    surfaceContainerLow = Color(0xFFF5F4FA),
    outline = Color(0xFFC7C5D0),
    error = Red40,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Red20,
)
