package com.kingzcheung.xime

import android.app.Application
import android.util.Log
import com.kingzcheung.xime.rime.RimeConfigHelper
import com.kingzcheung.xime.rime.RimeEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class XimeApplication : Application() {
    
    companion object {
        private const val TAG = "XimeApplication"
    }
    
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        writeStartupLog("XimeApplication.onCreate START")
        preInitializeRimeEngine()
        writeStartupLog("XimeApplication.onCreate DONE")
        
        Log.d(TAG, "Initialization complete")
    }
    
    private fun preInitializeRimeEngine() {
        if (RimeEngine.isInitialized()) {
            writeStartupLog("RimeEngine already initialized, skipping")
            Log.d(TAG, "Rime engine already initialized")
            return
        }
        
        if (!RimeEngine.isNativeLibraryLoaded()) {
            writeStartupLog("ERROR: rime_jni not loaded, skipping init")
            Log.e(TAG, "Native library rime_jni not loaded, cannot initialize Rime")
            return
        }
        
        writeStartupLog("Pre-initializing Rime engine...")
        Log.d(TAG, "Pre-initializing Rime engine...")
        applicationScope.launch {
            try {
                val (userDataDir, sharedDataDir) = RimeConfigHelper.initializeRimeDataAsync(this@XimeApplication)
                RimeEngine.getInstance().initialize(userDataDir, sharedDataDir)
                writeStartupLog("Rime engine pre-init completed OK")
                Log.d(TAG, "Rime engine pre-initialization completed")
            } catch (e: Exception) {
                writeStartupLog("Rime pre-init FAILED: ${e.message}")
                Log.e(TAG, "Failed to pre-initialize Rime engine", e)
            }
        }
    }
    
    private fun writeStartupLog(msg: String) {
        try {
            val logFile = java.io.File(filesDir, "startup.log")
            val timestamp = java.text.SimpleDateFormat("MM-dd HH:mm:ss.SSS", java.util.Locale.US).format(java.util.Date())
            logFile.appendText("[$timestamp] $msg\n")
        } catch (_: Exception) {
            // Best effort, don't crash
        }
    }
}