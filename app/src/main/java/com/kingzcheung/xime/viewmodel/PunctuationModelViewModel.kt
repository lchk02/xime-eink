package com.kingzcheung.xime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.xime.settings.SettingsPreferences
import com.kingzcheung.xime.speech.punctuation.PunctuationModelManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PunctuationModelUiState(
    val isDownloaded: Boolean = false,
    val downloadState: PunctuationModelManager.DownloadState = PunctuationModelManager.DownloadState.Idle,
    val isEnabled: Boolean = false
)

class PunctuationModelViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val punctuationManager = PunctuationModelManager(context)
    
    private val _uiState = MutableStateFlow(PunctuationModelUiState(
        isDownloaded = punctuationManager.isModelDownloaded(),
        isEnabled = SettingsPreferences.isPunctuationModelEnabled(context)
    ))
    val uiState: StateFlow<PunctuationModelUiState> = _uiState.asStateFlow()
    
    fun downloadModel() {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadState = PunctuationModelManager.DownloadState.Downloading(0f, 0L, -1L)) }
            
            withContext(Dispatchers.IO) {
                punctuationManager.downloadModel { state ->
                    _uiState.update { it.copy(downloadState = state) }
                    
                    if (state is PunctuationModelManager.DownloadState.Complete) {
                        _uiState.update { it.copy(
                            isDownloaded = true,
                            downloadState = PunctuationModelManager.DownloadState.Idle
                        )}
                    }
                }
            }
        }
    }
    
    fun deleteModel() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                punctuationManager.deleteModel()
            }
            
            SettingsPreferences.setPunctuationModelEnabled(context, false)
            
            _uiState.update { it.copy(
                isDownloaded = false,
                isEnabled = false
            )}
        }
    }
    
    fun setEnabled(enabled: Boolean) {
        SettingsPreferences.setPunctuationModelEnabled(context, enabled)
        _uiState.update { it.copy(isEnabled = enabled) }
    }
}