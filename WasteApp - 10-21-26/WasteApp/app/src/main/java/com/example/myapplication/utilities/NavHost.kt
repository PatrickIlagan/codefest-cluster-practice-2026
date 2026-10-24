package com.example.myapplication.utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.LocationEntriesActivity
import com.example.myapplication.MainActivity
import com.example.myapplication.ProfileActivity
import com.example.myapplication.R
import com.example.myapplication.WasteEntriesActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNav(bottomNav: BottomNavigationView, currentItemId: Int){
    bottomNav.selectedItemId = currentItemId

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == currentItemId ) return@setOnItemSelectedListener true

        val target = when (item.itemId) {
            R.id.menu_home -> MainActivity::class.java
            R.id.menu_wastes -> WasteEntriesActivity::class.java
                R.id.menu_profile -> ProfileActivity::class.java
                R.id.menu_locations -> LocationEntriesActivity::class.java
            else -> null

        }
        target?.let {
            startActivity(Intent(this, it))
            finish()
            overridePendingTransition(0,0)
        }
        true
    }
}