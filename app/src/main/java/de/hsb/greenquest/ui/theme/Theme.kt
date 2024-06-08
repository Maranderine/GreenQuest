package de.hsb.greenquest.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50), // Green color for primary elements
    onPrimary = Color.White, // Text on primary elements
    secondary = Color(0xFF689F38), // Darker green for secondary elements
    onSecondary = Color.White, // Text on secondary elements
    background = Color(0xFF2C3E50), // Dark background color
    onBackground = Color.White, // Text on background
    surface = Color(0xFF34495E), // Darker surface color
    onSurface = Color.White, // Text on surface
    error = Color(0xFFD32F2F), // Red color for error states
    onError = Color.White, // Text on error states
    outline = Color(0xFFBDBDBD), // Light gray for outlines
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // Green color for primary elements
    onPrimary = Color.White, // Text on primary elements
    secondary = Color(0xFF689F38), // Darker green for secondary elements
    onSecondary = Color.White, // Text on secondary elements
    background = Color(0xFFFDECCF), // Dark background color
    onBackground = Color.Black, // Text on background
    surface = Color(0xFF34495E), // Darker surface color
    onSurface = Color.White, // Text on surface
    error = Color(0xFFD32F2F), // Red color for error states
    onError = Color.White, // Text on error states
    outline = Color(0xFFBDBDBD) // Light gray for outlines
)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40,
//
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

@Composable
fun GreenQuestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}