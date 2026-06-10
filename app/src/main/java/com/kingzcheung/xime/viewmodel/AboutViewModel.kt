package com.kingzcheung.xime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.xime.update.UpdateChecker
import com.kingzcheung.xime.update.UpdateResult
import com.kingzcheung.xime.util.FileLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AboutUiState(
    val updateState: UpdateResult? = null,
    val isChecking: Boolean = false
)

class AboutViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AboutUiState())
    val uiState: StateFlow<AboutUiState> = _uiState.asStateFlow()
    
    fun checkForUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            
            val result = UpdateChecker.checkForUpdate()
            
            if (result is UpdateResult.Error) {
                FileLogger.e("UpdateCheck", "检查更新失败: ${result.message}")
            }
            
            _uiState.update { it.copy(
                updateState = result,
                isChecking = false
            )}
        }
    }
    
    fun clearUpdateState() {
        _uiState.update { it.copy(updateState = null) }
    }
}