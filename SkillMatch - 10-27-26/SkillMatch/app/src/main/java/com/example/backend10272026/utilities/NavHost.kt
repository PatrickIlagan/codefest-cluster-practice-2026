package com.example.backend10272026.utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.backend10272026.BookmarksActivity
import com.example.backend10272026.MainActivity
import com.example.backend10272026.OpportunitiesActivity
import com.example.backend10272026.ProfileActivity
import com.example.backend10272026.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNav(bottomNav: BottomNavigationView, currentItemId: Int) {
    bottomNav.selectedItemId = currentItemId

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == currentItemId) return@setOnItemSelectedListener true

        val target = when (item.itemId) {
            R.id.menuHome -> MainActivity::class.java
            R.id.menuUser -> ProfileActivity::class.java
            R.id.menuSaved -> BookmarksActivity::class.java
            R.id.menuOpportunities -> OpportunitiesActivity::class.java
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