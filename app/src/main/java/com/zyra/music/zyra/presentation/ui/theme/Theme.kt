package com.zyra.music.zyra.presentation.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    // Primary brand color
    primary = NeonGreen,
    onPrimary = DeepBlack,

    // Secondary color
    secondary = LightGray,
    onSecondary = PureWhite,

    // Tertiary color (optional accent)
    tertiary = LightGray,
    onTertiary = DeepBlack,

    // Backgrounds
    background = DeepBlack,
    onBackground = PureWhite,

    // Surfaces (cards, sheets, etc.)
    surface = DeepBlack,
    onSurface = PureWhite,

    // Variant surfaces (NavigationBar, elevated components)
    surfaceVariant = DeepBlack,
    onSurfaceVariant = LightGray,

    // Container variants for emphasis
    primaryContainer = NeonGreen,
    onPrimaryContainer = DeepBlack,
    secondaryContainer = LightGray,
    onSecondaryContainer = DeepBlack,
    tertiaryContainer = LightGray,
    onTertiaryContainer = DeepBlack,

    // Outline colors (borders, dividers)
    outline = LightGray,
    outlineVariant = LightGray.copy(alpha = 0.5f),

    // Error colors (optional)
    error = Color(0xFFFF4C4C),
    onError = DeepBlack,
    errorContainer = Color(0xFF660000),
    onErrorContainer = PureWhite,

    // Inverse colors (for contrasting text on light surfaces in dark mode)
    inverseOnSurface = DeepBlack,
    inverseSurface = NeonGreen,
    inversePrimary = DeepBlack,

    // Scrim (modal overlays)
    scrim = Color.Black.copy(alpha = 0.6f)
)
private val MonochromaticColorScheme = darkColorScheme(
    // Primary "accent" is now white
    primary = PureWhite,
    onPrimary = PureBlack,

    // Secondary elements are gray for less emphasis
    secondary = MidGray,
    onSecondary = PureBlack,

    // Tertiary is also gray for consistency
    tertiary = MidGray,
    onTertiary = PureBlack,

    // Backgrounds are pure black
    background = PureBlack,
    onBackground = PureWhite,

    // Surfaces (like Cards) are pure black
    surface = PureBlack,
    onSurface = PureWhite,

    // Variants can use gray for subtle differentiation
    surfaceVariant = DarkGray,
    onSurfaceVariant = MidGray,

    // Containers follow the same logic
    primaryContainer = PureWhite,
    onPrimaryContainer = PureBlack,
    secondaryContainer = MidGray,
    onSecondaryContainer = PureBlack,
    tertiaryContainer = MidGray,
    onTertiaryContainer = PureBlack,

    // Outlines for borders and dividers
    outline = DarkGray,

    // Error colors remain red as requested
    error = RedError,
    onError = PureWhite,
    errorContainer = RedError,
    onErrorContainer = PureWhite,

    // Inverse colors for special cases
    inverseSurface = PureWhite,
    inverseOnSurface = PureBlack
)
@Composable
fun ZyraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme : ColorScheme = when {
        darkTheme -> MonochromaticColorScheme
        else -> MonochromaticColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        // This SideEffect will run when the composable is first displayed
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to transparent
            window.statusBarColor = Color.Black.toArgb()
            // Set icons to be light (for dark backgrounds)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}