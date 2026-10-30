package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkSlateColorScheme = darkColorScheme(
    primary = StreakGreen,
    onPrimary = Color.Black,
    primaryContainer = StreakGreenMuted,
    onPrimaryContainer = StreakGreenLight,
    secondary = CyanAccent,
    onSecondary = Color.Black,
    secondaryContainer = SlateSurfaceVariant,
    onSecondaryContainer = SkyAccent,
    tertiary = FlameOrange,
    onTertiary = Color.White,
    background = SlateBackground,
    onBackground = TextPrimary,
    surface = SlateCard,
    onSurface = TextPrimary,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = SlateCardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // We enforce the designated Dark Slate theme (#0F172A with Green Streaks #22C55E)
    MaterialTheme(
        colorScheme = DarkSlateColorScheme,
        typography = Typography,
        content = content
    )
}
