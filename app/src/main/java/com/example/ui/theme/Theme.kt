package com.example.ui.theme

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
    primary = CareMintTeal,
    onPrimary = Color(0xFF003828),
    primaryContainer = CareMintTealContainer,
    onPrimaryContainer = CareMintTealOnContainer,
    secondary = CareCyan,
    onSecondary = Color(0xFF00363F),
    tertiary = CarePurple,
    background = CareNavyDark,
    onBackground = TextPrimaryDark,
    surface = CareNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = CareNavyContainer,
    onSurfaceVariant = TextSecondaryDark,
    outline = CareNavyBorder,
    error = CareRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = BaseEmerald500,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCFCE7),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF0E7490),
    onSecondary = Color.White,
    tertiary = CarePurple,
    background = BasePageBg,
    onBackground = BaseTextHeading,
    surface = BaseSurface,
    onSurface = BaseTextHeading,
    surfaceVariant = BaseRaisedSurface,
    onSurfaceVariant = BaseTextMuted,
    outline = BaseBorder,
    error = CareRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Base44 exact dark theme: #020617 page background, #0A1022 surface
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
