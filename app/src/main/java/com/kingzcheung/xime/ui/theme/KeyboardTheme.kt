package com.kingzcheung.xime.ui.theme

import androidx.compose.ui.graphics.Color

data class KeyboardColorScheme(
    val id: String,
    val name: String,
    val specialKeyLight: Color,
    val specialKeyDark: Color,
    val accentLight: Color,
    val accentDark: Color,
    val primaryLight: Color = accentLight,
    val primaryDark: Color = accentDark,
    val primaryContainerLight: Color = specialKeyLight,
    val primaryContainerDark: Color = specialKeyDark,
    val surfaceLight: Color = Color.White,
    val surfaceDark: Color = Color(0xFF1C1B1F)
)

object KeyboardThemes {
    val themes = listOf(
        KeyboardColorScheme(
            id = "eink",
            name = "简约水墨",
            specialKeyLight = Color(0xFFF0F0F0),
            specialKeyDark = Color(0xFF333333),
            accentLight = Color(0xFF000000),
            accentDark = Color(0xFFE0E0E0),
            primaryLight = Color(0xFF000000),
            primaryDark = Color(0xFFE8E8E8),
            primaryContainerLight = Color(0xFFF5F5F5),
            primaryContainerDark = Color(0xFF2D2D2D),
            surfaceLight = Color(0xFFFFFFFF),
            surfaceDark = Color(0xFF1C1B1F)
        )
    )
    
    fun getThemeById(id: String): KeyboardColorScheme {
        return themes.find { it.id == id } ?: themes[0]
    }
    
    fun getSpecialKeyColor(themeId: String, isDark: Boolean): Color {
        val theme = getThemeById(themeId)
        return if (isDark) theme.specialKeyDark else theme.specialKeyLight
    }
    
    fun getAccentColor(themeId: String, isDark: Boolean): Color {
        val theme = getThemeById(themeId)
        return if (isDark) theme.accentDark else theme.accentLight
    }
    
    fun getPrimaryColor(themeId: String, isDark: Boolean): Color {
        val theme = getThemeById(themeId)
        return if (isDark) theme.primaryDark else theme.primaryLight
    }
    
    fun getPrimaryContainerColor(themeId: String, isDark: Boolean): Color {
        val theme = getThemeById(themeId)
        return if (isDark) theme.primaryContainerDark else theme.primaryContainerLight
    }
    
    fun getSurfaceColor(themeId: String, isDark: Boolean): Color {
        val theme = getThemeById(themeId)
        return if (isDark) theme.surfaceDark else theme.surfaceLight
    }
}