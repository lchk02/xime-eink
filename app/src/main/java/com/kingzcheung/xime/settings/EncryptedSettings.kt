package com.kingzcheung.xime.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.util.concurrent.atomic.AtomicBoolean

object EncryptedSettings {
    private const val PREFS_NAME = "kime_encrypted"
    private const val LEGACY_PREFS_NAME = "kime_settings"
    private const val KEY_WEBDAV_PASSWORD = "webdav_password"
    private const val KEY_FUNASR_API_KEY = "funasr_api_key"

    private val migrated = AtomicBoolean(false)

    private fun getPrefs(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val encrypted = EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        migrateFromLegacyIfNeeded(context, encrypted)
        return encrypted
    }

    private fun migrateFromLegacyIfNeeded(context: Context, encrypted: SharedPreferences) {
        if (!migrated.compareAndSet(false, true)) return
        val legacy = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
        if (!legacy.contains(KEY_WEBDAV_PASSWORD) && !legacy.contains(KEY_FUNASR_API_KEY)) return

        val pw = legacy.getString(KEY_WEBDAV_PASSWORD, null)
        if (pw != null && !encrypted.contains(KEY_WEBDAV_PASSWORD)) {
            encrypted.edit().putString(KEY_WEBDAV_PASSWORD, pw).apply()
        }
        val key = legacy.getString(KEY_FUNASR_API_KEY, null)
        if (key != null && !encrypted.contains(KEY_FUNASR_API_KEY)) {
            encrypted.edit().putString(KEY_FUNASR_API_KEY, key).apply()
        }
        legacy.edit()
            .remove(KEY_WEBDAV_PASSWORD)
            .remove(KEY_FUNASR_API_KEY)
            .apply()
    }

    fun getWebDavPassword(context: Context): String {
        return getPrefs(context).getString(KEY_WEBDAV_PASSWORD, "") ?: ""
    }

    fun setWebDavPassword(context: Context, password: String) {
        getPrefs(context).edit().putString(KEY_WEBDAV_PASSWORD, password).apply()
    }

    fun getFunAsrApiKey(context: Context): String {
        return getPrefs(context).getString(KEY_FUNASR_API_KEY, "") ?: ""
    }

    fun setFunAsrApiKey(context: Context, apiKey: String) {
        getPrefs(context).edit().putString(KEY_FUNASR_API_KEY, apiKey).apply()
    }
}
