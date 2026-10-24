package com.example.practice20261022.data

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment // 👈 Changed from android.app.Fragment

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val dao by lazy { database.appDao() }
}

val Activity.dao get() = (application as App).dao
val Fragment.dao get() = (requireContext().applicationContext as App).dao