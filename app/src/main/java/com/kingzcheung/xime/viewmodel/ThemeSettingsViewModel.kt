package com.kingzcheung.xime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kingzcheung.xime.settings.SettingsPreferences
import com.kingzcheung.xime.ui.theme.KeyboardColorScheme
import com.kingzcheung.xime.ui.theme.KeyboardThemes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ThemeUiState(
    val darkMode: Int = 0,
    val colorTheme: String = "eink",
    val colorThemes: List<KeyboardColorScheme> = KeyboardThemes.themes,
    val showBottomButtons: Boolean = false,
    val hideBottomSpace: Boolean = false
)

class ThemeSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    
    private val _uiState = MutableStateFlow(ThemeUiState(
        darkMode = SettingsPreferences.getDarkMode(context),
        colorTheme = SettingsPreferences.getKeyboardTheme(context),
        showBottomButtons = SettingsPreferences.showBottomButtons(context),
        hideBottomSpace = SettingsPreferences.isHideBottomSpace(context)
    ))
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()
    
    fun setDarkMode(mode: Int) {
        SettingsPreferences.setDarkMode(context, mode)
        _uiState.update { it.copy(darkMode = mode) }
    }
    
    fun setColorTheme(themeId: String) {
        SettingsPreferences.setKeyboardTheme(context, themeId)
        _uiState.update { it.copy(colorTheme = themeId) }
    }
    
    fun setShowBottomButtons(show: Boolean) {
        SettingsPreferences.setShowBottomButtons(context, show)
        _uiState.update { it.copy(showBottomButtons = show) }
    }
    
    fun setHideBottomSpace(hide: Boolean) {
        SettingsPreferences.setHideBottomSpace(context, hide)
        _uiState.update { it.copy(hideBottomSpace = hide) }
    }
}