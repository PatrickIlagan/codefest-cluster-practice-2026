package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityProfileBinding
import com.example.myapplication.utilities.SessionManager
import com.example.myapplication.utilities.setupBottomNav

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val user = dao.getUserById(userId)
        if (user == null) {
            session.logout()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
            return
        }

        binding.txvProfileName.text = "${user.name}"
        binding.txvProfileEmail.text = "${user.email}"
        binding.txvProfileId.text = "ID: ${user.id}"

        setupBottomNav(
            binding.bottomNav,
            currentItemId = R.id.menu_profile,
        )

        binding.btnLogout.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }

        binding.textView19.setOnClickListener {
            session.logout()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}