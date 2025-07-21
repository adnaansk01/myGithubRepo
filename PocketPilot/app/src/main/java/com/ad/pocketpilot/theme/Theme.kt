package com.ad.pocketpilot.ui.theme

import android.app.Activity
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

/*private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

*//*private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    *//**//* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    *//**//*
)*//*

@Composable
fun PocketPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
*//*    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*//*

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    background = Background,
    surface = Surface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = TextColor,
    onSurface = TextColor
)*/

// 🎨 Custom Colors
val Almond = Color(0xFFF5E8DD)
val Cream = Color(0xFFFFF8F1)
val SoftGreen = Color(0xFF4CAF50)
val SoftRed = Color(0xFFF28B82)
val SoftBlue = Color(0xFFAECBFA)
val OnLightBackground = Color(0xFF1C1C1C)
val OnDarkBackground = Color(0xFFEDEDED)

private val LightColorScheme = lightColorScheme(
    primary = SoftGreen,
    secondary = SoftRed,
    background = Cream,
    surface = Color.White,
    onBackground = OnLightBackground,
    onPrimary = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftGreen,
    secondary = SoftRed,
    background = Color(0xFF121212),
    surface = Color(0xFF1F1F1F),
    onBackground = OnDarkBackground,
    onPrimary = Color.White
)

@Composable
fun PocketPilotTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}