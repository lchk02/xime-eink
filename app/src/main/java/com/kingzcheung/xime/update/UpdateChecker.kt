package com.kingzcheung.xime.update

import android.util.Log
import com.kingzcheung.xime.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ReleaseInfo(
    val versionName: String,
    val htmlUrl: String,
    val publishedAt: String,
    val releaseNotes: String
)

sealed class UpdateResult {
    data class UpdateAvailable(val release: ReleaseInfo) : UpdateResult()
    data object NoUpdate : UpdateResult()
    data class Error(val message: String) : UpdateResult()
}

object UpdateChecker {
    private const val TAG = "UpdateChecker"
    private const val GITHUB_OWNER = "lchk02"
    private const val GITHUB_REPO = "xime-eink"
    private const val API_URL = "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    suspend fun checkForUpdate(): UpdateResult = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Checking for updates from $API_URL")
            
            val request = Request.Builder()
                .url(API_URL)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "Xime/${BuildConfig.VERSION_NAME}")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                val errorMsg = "HTTP ${response.code}: ${response.message}"
                Log.e(TAG, "Failed to fetch release info: $errorMsg")
                return@withContext UpdateResult.Error(errorMsg)
            }
            
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty()) {
                Log.e(TAG, "Empty response body")
                return@withContext UpdateResult.Error("Empty response from server")
            }
            
            val json = JSONObject(responseBody)
            val tagName = json.getString("tag_name")
            val htmlUrl = json.getString("html_url")
            val publishedAt = json.optString("published_at", "")
            val releaseNotes = json.optString("body", "")
            
            val latestVersion = tagName.removePrefix("v").removePrefix("V")
            val currentVersion = BuildConfig.VERSION_NAME
            
            Log.d(TAG, "Current version: $currentVersion, Latest version: $latestVersion")
            
            val release = ReleaseInfo(
                versionName = latestVersion,
                htmlUrl = htmlUrl,
                publishedAt = publishedAt,
                releaseNotes = releaseNotes
            )
            
            if (compareVersions(currentVersion, latestVersion) < 0) {
                Log.i(TAG, "Update available: $latestVersion")
                UpdateResult.UpdateAvailable(release)
            } else {
                Log.i(TAG, "No update available")
                UpdateResult.NoUpdate
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking for updates", e)
            UpdateResult.Error(e.message ?: "Unknown error")
        }
    }
    
    private fun compareVersions(current: String, latest: String): Int {
        val currentParts = current.split(".")
        val latestParts = latest.split(".")
        
        val maxLen = maxOf(currentParts.size, latestParts.size)
        
        for (i in 0 until maxLen) {
            val currentPart = currentParts.getOrNull(i)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
            val latestPart = latestParts.getOrNull(i)?.filter { it.isDigit() }?.toIntOrNull() ?: 0
            
            if (currentPart < latestPart) return -1
            if (currentPart > latestPart) return 1
        }
        
        return 0
    }
}