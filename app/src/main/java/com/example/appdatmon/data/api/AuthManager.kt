package com.example.appdatmon.data.api

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "AuthPrefs"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "auth_role"
    private const val KEY_USER_NAME = "auth_user_name"
    private const val KEY_USER_ID = "auth_user_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    var token: String? = null
    var role: String? = null
    var userName: String? = null
    var userId: Long = -1L

    fun saveAuth(context: Context, token: String?, role: String?, userName: String?, userId: Long = -1L) {
        this.token = token
        this.role = role
        this.userName = userName
        this.userId = userId
        getPrefs(context).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role)
            .putString(KEY_USER_NAME, userName)
            .putLong(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(context: Context): Long {
        if (userId == -1L) {
            userId = getPrefs(context).getLong(KEY_USER_ID, -1L)
        }
        return userId
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

    fun getUserName(context: Context): String? {
        if (userName == null) {
            userName = getPrefs(context).getString(KEY_USER_NAME, null)
        }
        return userName
    }

    fun clear(context: Context) {
        token = null
        role = null
        userName = null
        getPrefs(context).edit().clear().apply()
    }
}
