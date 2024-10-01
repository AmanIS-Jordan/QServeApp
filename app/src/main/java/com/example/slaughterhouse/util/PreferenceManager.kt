package com.example.slaughterhouse.util

import android.content.Context
import android.content.SharedPreferences

object PreferenceManager {

    private const val PREF_NAME = "user_prefs"
    private const val KEY_NAME = "user_name"
    private const val COUNTER_ID = "counter_id"
    private const val SELECTED_BRANCH = "selected_branch"

    private const val KEY_PASSWORD = "user_password"
    private const val KEY_BASE_URL = "base_url"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_ADDED_URL = "is_AddedUrl"

    private const val KEY_IS_SELECTED_TICKET = "is_selected_ticket"
    private const val KEY_SELECTED_TICKET = "selected_ticket"




    // Initialize SharedPreferences
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun setSelectedTicket(context: Context, isSelectedTicket: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_SELECTED_TICKET, isSelectedTicket)
        editor.apply()
    }

    fun saveSelectedTicket(context: Context, selectedTicket: String , isSelectedTicket: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_SELECTED_TICKET, selectedTicket)
        editor.putBoolean(KEY_IS_SELECTED_TICKET, isSelectedTicket)
        editor.apply()
    }



    // Check if the user is logged in
    fun isSelectedTicket(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_SELECTED_TICKET, false)
    }



    // Save user login status
    fun setLoggedIn(context: Context, isLoggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    fun setURl(context: Context, isAddedUrl: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putBoolean(KEY_IS_ADDED_URL, isAddedUrl)
        editor.apply()
    }

    // Check if the user is logged in
    fun isLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Save user name and password
    fun saveUserInfo(context: Context, name: String, counterId : String , selectedBranch: String, isLoggedIn: Boolean) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_NAME, name)
        editor.putString(COUNTER_ID, counterId)
        editor.putString(SELECTED_BRANCH, selectedBranch)
        editor.putBoolean(KEY_IS_LOGGED_IN,isLoggedIn)
        editor.apply() // Save data asynchronously
    }

    // Save base URL
    fun saveBaseUrl(context: Context, baseUrl: String , isAddedUrl : Boolean) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_BASE_URL, baseUrl)
        editor.putBoolean(KEY_IS_ADDED_URL, isAddedUrl)

        editor.apply()
    }

    fun isAddedURL(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_ADDED_URL, false)
    }

    // Get saved base URL
    fun getBaseUrl(context: Context): String? {
        return getPreferences(context).getString(KEY_BASE_URL, "https://defaulturl.com") // Default value if not set
    }

    // Get saved user name
    fun getUserName(context: Context): String? {
        return getPreferences(context).getString(KEY_NAME, null)
    }



    // Get saved user password
    fun getUserPassword(context: Context): String? {
        return getPreferences(context).getString(KEY_PASSWORD, null)
    }


    fun getCounterId(context: Context): String? {
        return getPreferences(context).getString(COUNTER_ID, null)
    }

    fun getSelectedBranch(context: Context): String? {
        return getPreferences(context).getString(SELECTED_BRANCH, null)
    }




    // Clear user info (e.g., when logging out)
    fun clearUserInfo(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_NAME)
        editor.remove(COUNTER_ID)
        editor.remove(SELECTED_BRANCH)
        editor.remove(KEY_IS_LOGGED_IN)
        editor.apply()
    }

    fun clearUrl(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_BASE_URL)
        editor.apply()
    }
}
