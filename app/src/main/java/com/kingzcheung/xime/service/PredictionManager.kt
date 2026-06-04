package com.kingzcheung.xime.service

import android.content.Context
import com.kingzcheung.xime.util.FileLogger
import kotlinx.coroutines.CoroutineScope

class PredictionManager(
    private val context: Context,
    private val serviceScope: CoroutineScope,
    private val onStateChanged: (InputUIState) -> Unit,
    private val getState: () -> InputUIState
) {
    companion object {
        private const val TAG = "PredictionManager"
        private const val MAX_CONTEXT_LENGTH = 25
    }
    
    private var _lastCommittedText = ""
    val lastCommittedText: String get() = _lastCommittedText
    
    fun appendCommittedText(text: String) {
        _lastCommittedText = (_lastCommittedText + text).takeLast(MAX_CONTEXT_LENGTH)
        FileLogger.d(TAG, "Context updated: '$text' -> '$lastCommittedText' (len=${lastCommittedText.length})")
    }
    
    fun clearCommittedText() {
        _lastCommittedText = ""
    }
    
    fun deleteLastChar() {
        if (_lastCommittedText.isNotEmpty()) {
            _lastCommittedText = _lastCommittedText.dropLast(1)
        }
    }
    
    fun initialize() {
        FileLogger.i(TAG, "PredictionManager initialized")
    }
}
