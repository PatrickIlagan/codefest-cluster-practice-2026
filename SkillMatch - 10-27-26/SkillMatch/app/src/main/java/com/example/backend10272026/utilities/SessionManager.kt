package com.example.backend10272026.utilities

import android.content.Context

class SessionManager(context : Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "app_session",
        Context.MODE_PRIVATE
    )

    fun login(userID: Long, userName: String, role: String = "MEMBER"){
        prefs.edit()
            .putBoolean("logged", true)
            .putLong("userID", userID)
            .putString("userName", userName)
            .putString("role", role)
            .apply()
    }

    fun isLogged(): Boolean = prefs.getBoolean("logged", false)
    fun getUserID(): Long = prefs.getLong("userID", -1L)
    fun getUserName(): String = prefs.getString("userName", "") ?: ""
    fun getRole(): String = prefs.getString("role", "MEMBER") ?: "MEMBER"

    fun logout(){
        prefs.edit().clear().apply()
    }
}