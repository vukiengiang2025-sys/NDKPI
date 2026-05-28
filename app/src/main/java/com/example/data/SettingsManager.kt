package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    var geminiApiKey: String
        get() = prefs.getString("gemini_api_key", "") ?: ""
        set(value) = prefs.edit().putString("gemini_api_key", value).apply()

    var userName: String
        get() = prefs.getString("user_name", "Nam Dược") ?: "Nam Dược"
        set(value) = prefs.edit().putString("user_name", value).apply()

    var isReminderEnabled: Boolean
        get() = prefs.getBoolean("daily_reminder", false)
        set(value) = prefs.edit().putBoolean("daily_reminder", value).apply()
    var selectedModel: String
        get() = prefs.getString("selected_model", "gemini-3.1-pro-preview") ?: "gemini-3.1-pro-preview"
        set(value) = prefs.edit().putString("selected_model", value).apply()
}
