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
        
        preInitializeRimeEngine()
        
        Log.d(TAG, "Initialization complete")
    }
    
    private fun preInitializeRimeEngine() {
        if (RimeEngine.isInitialized()) {
            Log.d(TAG, "Rime engine already initialized")
            return
        }
        
        Log.d(TAG, "Pre-initializing Rime engine...")
        applicationScope.launch {
            try {
                val (userDataDir, sharedDataDir) = RimeConfigHelper.initializeRimeDataAsync(this@XimeApplication)
                RimeEngine.getInstance().initialize(userDataDir, sharedDataDir)
                Log.d(TAG, "Rime engine pre-initialization completed")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pre-initialize Rime engine", e)
            }
        }
    }
}