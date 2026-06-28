package com.example.appdatmon.data.api

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "AuthPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "auth_role"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String? = null
    var role: String? = null

    fun saveAuth(context: Context, token: String?, role: String?) {
        this.token = token
        this.role = role
        getPrefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role)
            .apply()
    }

    fun getToken(context: Context): String? {
        if (token == null) {
            token = getPrefs(context).getString(KEY_TOKEN, null)
        }
        return token
    }

    fun getRole(context: Context): String? {
        if (role == null) {
            role = getPrefs(context).getString(KEY_ROLE, null)
        }
        return role
    }

    fun clear(context: Context) {
        token = null
        role = null
        getPrefs(context).edit().clear().apply()
    }
}
