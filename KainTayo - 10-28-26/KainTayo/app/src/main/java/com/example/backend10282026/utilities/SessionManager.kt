package com.example.frontend10282026.utilities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar

class SessionManager(context : Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "app_session",
        Context.MODE_PRIVATE
    )

    fun login(userID: Long, userName: String, role: String = "CUSTOMER") {
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
    fun getUserRole(): String = prefs.getString("role", "CUSTOMER") ?: "CUSTOMER"

    fun logout() {
        prefs.edit().clear().apply()
    }
}

