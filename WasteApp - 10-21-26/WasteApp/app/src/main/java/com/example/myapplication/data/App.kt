package com.example.myapplication.data

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment

class App : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val dao by lazy { database.appDao()}
}
val Activity.dao get() = (application as App).dao
val Fragment.dao get() = (requireActivity().application as App).dao