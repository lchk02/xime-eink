package com.kingzcheung.xime.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.kingzcheung.xime.settings.SettingsPreferences

// ========== 统一主题系统 ==========

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
        onPrimary = if (currentThemeId == "eink") Color.Black else Color.White,
        primaryContainer = theme.primaryContainerLight,
        onPrimaryContainer = if (currentThemeId == "eink") Color.Black else Color(0xFF21005D),
        secondary = theme.primaryContainerLight,
        onSecondary = if (currentThemeId == "eink") Color.Black else Color(0xFF21005D),
        tertiary = theme.primaryContainerLight,
        onTertiary = if (currentThemeId == "eink") Color.Black else Color(0xFF21005D),
        background = Color.White,
        onBackground = Color(0xFF1C1B1F),
        surface = theme.surfaceLight,
        onSurface = Color(0xFF1C1B1F),
        surfaceVariant = theme.primaryContainerLight.copy(alpha = 0.5f),
        onSurfaceVariant = if (currentThemeId == "eink") Color.Black else Color(0xFF49454F),
        outline = if (currentThemeId == "eink") Color(0xFF333333) else Color(0xFF79747E),
        outlineVariant = if (currentThemeId == "eink") Color(0xFF777777) else Color(0xFFCAC4D0)
    )
    
    val darkScheme = darkColorScheme(
        primary = theme.primaryDark,
        onPrimary = if (currentThemeId == "eink") Color.Black else Color(0xFF381E72),
        primaryContainer = theme.primaryContainerDark,
        onPrimaryContainer = if (currentThemeId == "eink") Color(0xFFE0E0E0) else Color(0xFFEADDFF),
        secondary = theme.primaryContainerDark,
        onSecondary = if (currentThemeId == "eink") Color(0xFFE0E0E0) else Color(0xFFEADDFF),
        tertiary = theme.primaryContainerDark,
        onTertiary = if (currentThemeId == "eink") Color(0xFFE0E0E0) else Color(0xFFEADDFF),
        background = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
        surface = theme.surfaceDark,
        onSurface = if (currentThemeId == "eink") Color(0xFFE0E0E0) else Color(0xFFE6E1E5),
        surfaceVariant = theme.primaryContainerDark.copy(alpha = 0.5f),
        onSurfaceVariant = if (currentThemeId == "eink") Color(0xFFAAAAAA) else Color(0xFFCAC4D0),
        outline = if (currentThemeId == "eink") Color(0xFF555555) else Color(0xFF938F99),
        outlineVariant = if (currentThemeId == "eink") Color(0xFF444444) else Color(0xFF49454F)
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkScheme else lightScheme,
        content = content
    )
}