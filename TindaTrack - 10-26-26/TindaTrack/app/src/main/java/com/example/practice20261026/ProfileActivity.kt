package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityProductBinding
import com.example.practice20261026.databinding.ActivityProfileBinding
import com.example.practice20261026.utilities.SessionManager
import com.example.practice20261026.utilities.setupBottomNav

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val user = dao.getUserById(userId)

        if (user != null){
            binding.txvEmail.text = user.email
            binding.txvUsername.text = user.name
        }

        binding.lilTransactionHistory.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
            finish()
        }
        binding.lilProducts.setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }
        binding.lilLogOut.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        setupBottomNav(binding.bottomNav, R.id.navProfile)
    }
}