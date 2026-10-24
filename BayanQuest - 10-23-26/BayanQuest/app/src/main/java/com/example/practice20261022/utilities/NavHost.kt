package com.example.practice20261022.utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.practice20261022.MainActivity
import com.example.practice20261022.MissionsActivity
import com.example.practice20261022.ProfileActivity
import com.example.practice20261022.R
import com.example.practice20261022.ResourcesActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNav(bottomNav: BottomNavigationView, currentItemId: Int) {
    bottomNav.selectedItemId = currentItemId

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == currentItemId) return@setOnItemSelectedListener true

        val target = when(item.itemId){
            R.id.nav_home -> MainActivity::class.java
            R.id.nav_resource -> ResourcesActivity::class.java
            R.id.nav_profile -> ProfileActivity::class.java
            R.id.nav_mission -> MissionsActivity::class.java
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