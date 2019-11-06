package com.alexb.devicelocation.data

import com.alexb.devicelocation.framework.preferences.SharedPreferencesProxy

class SharedPreferencesDataSource(private val sharedPreferencesProxy: SharedPreferencesProxy) {

    fun save(key: String, value: String) {
        sharedPreferencesProxy.save(key, value)
    }

    fun load(key: String, default: String?): String? {
        return sharedPreferencesProxy.load(key, default)
    }
}