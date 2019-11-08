package com.alexb.devicelocation.framework.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE

class SharedPreferencesProxy(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    fun save(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun load(key: String, default: String?): String? {
        return sharedPreferences.getString(key, default)
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "CommonSharedPreferences"
    }
}
