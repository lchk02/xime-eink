package com.kingzcheung.xime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kingzcheung.xime.rime.RimeEngine
import com.kingzcheung.xime.settings.SchemaConfigHelper
import com.kingzcheung.xime.settings.SchemaInfo
import com.kingzcheung.xime.settings.SettingsPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SchemaUiState(
    val schemas: List<SchemaInfo> = emptyList(),
    val currentSchema: String = "wubi86",
    val downloadStatus: Map<String, Boolean> = emptyMap(),
    val downloadingSchema: String? = null,
    val isDeploying: Boolean = false,
    val toastMessage: String? = null
)

class SchemaSettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    
    private val _uiState = MutableStateFlow(SchemaUiState())
    val uiState: StateFlow<SchemaUiState> = _uiState.asStateFlow()
    
    init {
        loadSchemas()
    }
    
    private fun loadSchemas() {
        val builtInSchemas = SchemaConfigHelper.loadSchemas(context)
        val currentSchema = SettingsPreferences.getCurrentSchema(context)
        val downloadStatus = builtInSchemas.associate { schema ->
            schema.schemaId to !SchemaConfigHelper.needsDownload(context, schema.schemaId)
        }
        
        val schemas = builtInSchemas.map { schema ->
            SchemaInfo(
                schemaId = schema.schemaId,
                name = schema.name,
                version = schema.version,
                author = schema.author,
                description = schema.description,
                isDownloaded = downloadStatus[schema.schemaId] ?: false,
                needsUpdate = false
            )
        }
        
        _uiState.update { it.copy(
            schemas = schemas,
            currentSchema = currentSchema,
            downloadStatus = downloadStatus
        )}
    }
    
    fun selectSchema(schema: SchemaInfo) {
        val isDownloaded = _uiState.value.downloadStatus[schema.schemaId] ?: false
        if (!isDownloaded || _uiState.value.currentSchema == schema.schemaId) return
        
        SettingsPreferences.setCurrentSchema(context, schema.schemaId)
        _uiState.update { it.copy(currentSchema = schema.schemaId) }
        
        if (RimeEngine.isInitialized()) {
            val engine = RimeEngine.getInstance()
            val availableSchemas = engine.getAvailableSchemas()
            
            if (schema.schemaId in availableSchemas) {
                engine.switchSchema(schema.schemaId)
                showToast("已切换到${schema.name}")
            } else {
                showToast("请点击「部署」按钮")
            }
        }
    }
    
    fun downloadSchema(schema: SchemaInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadingSchema = schema.schemaId) }
            
            val success = withContext(Dispatchers.IO) {
                SchemaConfigHelper.downloadSchema(context, schema.schemaId)
            }
            
            _uiState.update { state ->
                val newStatus = state.downloadStatus + (schema.schemaId to true)
                val updatedSchemas = state.schemas.map { s ->
                    if (s.schemaId == schema.schemaId) s.copy(isDownloaded = true)
                    else s
                }
                state.copy(
                    downloadingSchema = null,
                    downloadStatus = newStatus,
                    schemas = updatedSchemas
                )
            }
            
            showToast(if (success) "${schema.name}下载完成，请部署" else "${schema.name}下载失败")
        }
    }
    
    fun updateSchema(schema: SchemaInfo) {
        viewModelScope.launch {
            _uiState.update { it.copy(downloadingSchema = schema.schemaId) }
            
            val success = withContext(Dispatchers.IO) {
                SchemaConfigHelper.downloadSchema(context, schema.schemaId)
            }
            
            _uiState.update { state ->
                val newStatus = state.downloadStatus + (schema.schemaId to true)
                val updatedSchemas = state.schemas.map { s ->
                    if (s.schemaId == schema.schemaId) s.copy(needsUpdate = false)
                    else s
                }
                state.copy(
                    downloadingSchema = null,
                    downloadStatus = newStatus,
                    schemas = updatedSchemas
                )
            }
            
            showToast(if (success) "${schema.name}更新完成" else "${schema.name}更新失败")
        }
    }
    
    fun deploySchema() {
        if (_uiState.value.isDeploying || _uiState.value.downloadingSchema != null) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isDeploying = true) }
            
            val success = withContext(Dispatchers.IO) {
                RimeEngine.getInstance().deploy()
            }
            
            _uiState.update { it.copy(isDeploying = false) }
            showToast(if (success) "部署完成" else "部署失败")
        }
    }
    
    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
    
    private fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }
}