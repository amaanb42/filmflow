/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory.ui.theme

import android.app.Activity
import android.graphics.Color
import android.hardware.lights.Light
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = dark_pine,
    onPrimary = dark_text,
    primaryContainer = dark_foam,
    onPrimaryContainer = dark_text,
    secondary = dark_gold,
    onSecondary = dark_text,
    // secondaryContainer = dark_rose,
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
fun InventoryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // Dynamic color in this app is turned off for learning purposes
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (darkTheme) {
                window.statusBarColor = Color.TRANSPARENT
                window.navigationBarColor = Color.TRANSPARENT
            } else {
                window.statusBarColor = Color.TRANSPARENT
                window.navigationBarColor = Color.TRANSPARENT
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
