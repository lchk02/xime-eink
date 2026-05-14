package com.kingzcheung.xime.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class LogViewerUiState(
    val logFiles: List<File> = emptyList(),
    val selectedLogFile: File? = null,
    val logContent: String = "",
    val isLoading: Boolean = false,
    val errorMsg: String? = null
)

class LogViewerViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val TAG = "LogViewerViewModel"
    
    private val _uiState = MutableStateFlow(LogViewerUiState())
    val uiState: StateFlow<LogViewerUiState> = _uiState.asStateFlow()
    
    init {
        loadLogFiles()
    }
    
    fun loadLogFiles() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMsg = null) }
            
            try {
                val logsDir = File(context.filesDir, "logs")
                if (!logsDir.exists()) {
                    logsDir.mkdirs()
                    Log.i(TAG, "Created logs directory: ${logsDir.absolutePath}")
                }
                
                val allFiles = withContext(Dispatchers.IO) {
                    logsDir.listFiles()?.toList() ?: emptyList()
                }
                
                val logFiles = allFiles
                    .filter { it.isFile && it.name.endsWith(".log") }
                    .sortedByDescending { it.lastModified() }
                
                _uiState.update { it.copy(
                    logFiles = logFiles,
                    isLoading = false
                )}
                
                if (logFiles.isNotEmpty()) {
                    selectLogFile(logFiles.first())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading log files", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMsg = e.message
                )}
            }
        }
    }
    
    fun selectLogFile(file: File) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedLogFile = file) }
            
            try {
                val content = withContext(Dispatchers.IO) {
                    file.readText()
                }
                
                _uiState.update { it.copy(
                    logContent = content,
                    isLoading = false
                )}
            } catch (e: Exception) {
                Log.e(TAG, "Error reading log file", e)
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMsg = "读取日志失败: ${e.message}"
                )}
            }
        }
    }
    
    fun deleteLogFile(file: File) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                file.delete()
            }
            
            _uiState.update { state ->
                val newFiles = state.logFiles.filter { it != file }
                state.copy(
                    logFiles = newFiles,
                    selectedLogFile = if (state.selectedLogFile == file) null else state.selectedLogFile,
                    logContent = if (state.selectedLogFile == file) "" else state.logContent
                )
            }
            
            if (_uiState.value.selectedLogFile == null && _uiState.value.logFiles.isNotEmpty()) {
                selectLogFile(_uiState.value.logFiles.first())
            }
        }
    }
    
    fun clearAllLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            withContext(Dispatchers.IO) {
                _uiState.value.logFiles.forEach { file ->
                    file.delete()
                }
            }
            
            _uiState.update { it.copy(
                logFiles = emptyList(),
                selectedLogFile = null,
                logContent = "",
                isLoading = false
            )}
        }
    }
}