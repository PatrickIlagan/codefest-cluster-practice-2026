package com.example.myapplication.utilities

import android.content.Context

class SessionManager(context: Context) {
   private val prefs = context.applicationContext.getSharedPreferences(
       "app_session",
       Context.MODE_PRIVATE
   )

    fun login(userId: Long, userName: String, role: String = "MEMBER"){
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putLong("user_id", userId)
            .putString("user_name", userName)
            .putString("role", role)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("is_logged_in", false)
    fun getUserId(): Long = prefs.getLong("user_id", -1L)
    fun getUserName(): String = prefs.getString("user_name", "") ?: ""
    fun getRole(): String = prefs.getString("role", "MEMBER") ?: "MEMBER"

    fun logout() {
        prefs.edit().clear().apply()
    }
}