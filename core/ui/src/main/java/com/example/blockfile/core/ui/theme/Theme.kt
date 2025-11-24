package com.example.blockfile.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Accent,           // Celeste de los botones
    onPrimary = TextOnAccent,   // Texto oscuro dentro del botón celeste

    primaryContainer = Accent100,
    onPrimaryContainer = TextMain,

    secondary = Accent600,
    onSecondary = TextOnAccent,

    background = DarkBg,        // Fondo general
    onBackground = TextMain,

    surface = DarkBg2,          // Tarjetas / sección central
    onSurface = TextMain,

    surfaceVariant = DarkSurface,
    onSurfaceVariant = TextMain,

    error = Danger,
    onError = TextOnAccent,
)

@Composable
fun BlockFileTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
