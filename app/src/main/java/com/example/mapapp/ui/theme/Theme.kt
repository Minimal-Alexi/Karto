package com.example.mapapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue500,
    secondary = Green500,
    tertiary = Orange500,
    error = Red500,
    background = Grey1000,
    onBackground = Grey100,
    surface = Grey800,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Green500,
    tertiary = Orange500,
    error = Red500,
    background = Grey150,
    onBackground = Grey1000,
    surface = Color.White,
    onSurface = Grey800,

    /* Other default colors to override
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MapAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // We don't use dynamic colors, we use own theme colors
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}