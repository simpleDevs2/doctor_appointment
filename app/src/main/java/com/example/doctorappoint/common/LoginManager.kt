package com.example.doctorappoint.common

import android.content.Context
import android.content.SharedPreferences
import com.example.doctorappoint.model.User
import com.google.gson.Gson

object LoginManager {
    private const val PREF_NAME = "login_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_DATA = "user_data"
    private const val KEY_TOKEN = "token"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginData(context: Context, user: User, token: String) {
        val prefs = getSharedPreferences(context)
        val gson = Gson()
        
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_DATA, gson.toJson(user))
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUser(context: Context): User? {
        val prefs = getSharedPreferences(context)
        val userJson = prefs.getString(KEY_USER_DATA, null)
        
        return if (userJson != null) {
            try {
                Gson().fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    fun getToken(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_TOKEN, null)
    }

    fun logout(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().clear().apply()
    }
} 