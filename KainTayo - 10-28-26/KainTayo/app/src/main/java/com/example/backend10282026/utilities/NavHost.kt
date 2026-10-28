package com.example.backend10282026.utilities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.backend10282026.MainActivity
import com.example.backend10282026.MenuActivity
import com.example.backend10282026.OrdersActivity
import com.example.backend10282026.ProfileActivity
import com.example.backend10282026.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun AppCompatActivity.setupBottomNav(bottomNav: BottomNavigationView, currentItemId: Int){
    bottomNav.selectedItemId = currentItemId

    bottomNav.setOnItemSelectedListener { item ->
        if (item.itemId == currentItemId) return@setOnItemSelectedListener true

        val target = when (item.itemId){
            R.id.menuHome -> MainActivity::class.java
            R.id.menuProfile -> ProfileActivity::class.java
            R.id.menuOrders -> OrdersActivity::class.java
            R.id.menuSearch -> MenuActivity::class.java
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