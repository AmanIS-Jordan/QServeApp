package com.example.slaughterhouse.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_NAME = "user_name"
    private const val KEY_PASSWORD = "user_password"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"


    // Initialize SharedPreferences
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    // Save user login status
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    // Check if the user is logged in
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Save user name and password
    fun saveUserInfo(context: Context, name: String, password: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_PASSWORD, password)
        editor.apply() // Save data asynchronously
    }

    // Save base URL
    fun saveBaseUrl(context: Context, baseUrl: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_BASE_URL, baseUrl)
        editor.apply()
    }

    // Get saved user name
    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_NAME, null)
    }

    // Get saved user password
    fun getUserPassword(context: Context): String? {
        return getPreferences(context).getString(KEY_PASSWORD, null)
    }

    // Get saved base URL
    fun getBaseUrl(context: Context): String? {
        return getPreferences(context).getString(KEY_BASE_URL, "https://defaulturl.com") // Default value if not set
    }

    // Clear user info (e.g., when logging out)
    fun clearUserInfo(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_NAME)
        editor.remove(KEY_PASSWORD)
        editor.remove(KEY_IS_LOGGED_IN)
        editor.apply()
    }

    fun clearUrl(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_BASE_URL)
        editor.apply()
    }
}
