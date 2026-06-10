package com.kingzcheung.xime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kingzcheung.xime.settings.SettingsPreferences

// ========== 统一主题系统 ==========

val LocalIsDarkTheme = compositionLocalOf { false }

@Composable
fun XimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeId: String? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentThemeId = themeId ?: SettingsPreferences.getKeyboardTheme(context)
    val theme = KeyboardThemes.getThemeById(currentThemeId)
    
    val lightScheme = lightColorScheme(
        primary = theme.primaryLight,
        onPrimary = Color.Black,
        primaryContainer = theme.primaryContainerLight,
        onPrimaryContainer = Color.Black,
        secondary = theme.primaryContainerLight,
        onSecondary = Color.Black,
        tertiary = theme.primaryContainerLight,
        onTertiary = Color.Black,
        background = Color.White,
        onBackground = Color(0xFF1C1B1F),
        surface = theme.surfaceLight,
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = theme.primaryContainerLight.copy(alpha = 0.5f),
        onSurfaceVariant = Color.Black,
        outline = Color(0xFF333333),
        outlineVariant = Color(0xFF777777)
    )
    
    val darkScheme = darkColorScheme(
        primary = theme.primaryDark,
        onPrimary = Color.Black,
        primaryContainer = theme.primaryContainerDark,
        onPrimaryContainer = Color(0xFFE0E0E0),
        secondary = theme.primaryContainerDark,
        onSecondary = Color(0xFFE0E0E0),
        tertiary = theme.primaryContainerDark,
        onTertiary = Color(0xFFE0E0E0),
        background = Color.Black,
        onBackground = Color(0xFFE6E1E5),
        surface = theme.surfaceDark,
        onSurface = Color(0xFFE0E0E0),
        surfaceVariant = theme.primaryContainerDark.copy(alpha = 0.5f),
        onSurfaceVariant = Color(0xFFAAAAAA),
        outline = Color(0xFF555555),
        outlineVariant = Color(0xFF444444)
    )

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkScheme else lightScheme,
            content = content
        )
    }
}