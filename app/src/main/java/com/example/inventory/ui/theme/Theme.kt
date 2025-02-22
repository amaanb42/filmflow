package com.example.inventory.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = dark_pine,
    onPrimary = dark_text,
    primaryContainer = dark_foam,
    onPrimaryContainer = dark_text,
    secondary = dark_gold,
    onSecondary = dark_text,
    secondaryContainer = dark_pine,
    onSecondaryContainer = dark_text,
    tertiary = dark_iris,
    onTertiary = dark_text,
    tertiaryContainer = dark_highlight_med,
    onTertiaryContainer = dark_text,
    error = theme_delete_button,
    errorContainer = dark_love,
    onError = dark_text,
    onErrorContainer = dark_text,
    background = dark_base,
    onBackground = dark_text,
    surface = dark_surface,
    onSurface = dark_text,
    surfaceVariant = dark_overlay,
    onSurfaceVariant = dark_text,
    outline = dark_pine
)

private val LightColorScheme = lightColorScheme(
    primary = light_pine,
    onPrimary = light_text,
    primaryContainer = light_foam,
    onPrimaryContainer = light_text,
    secondary = light_gold,
    onSecondary = light_text,
//    secondaryContainer = light_rose,
    onSecondaryContainer = light_text,
    secondaryContainer = light_pine,
    tertiary = light_iris,
    onTertiary = light_text,
    tertiaryContainer = light_highlight_med,
    onTertiaryContainer = light_text,
    error = theme_delete_button,
    errorContainer = light_love,
    onError = light_text,
    onErrorContainer = light_text,
    background = light_base,
    onBackground = light_text,
    surface = light_surface,
    onSurface = light_text,
    surfaceVariant = light_overlay,
    onSurfaceVariant = light_text,
    outline = light_pine
    )

@Composable
fun FilmFlowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enable dynamic color
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            if (darkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
