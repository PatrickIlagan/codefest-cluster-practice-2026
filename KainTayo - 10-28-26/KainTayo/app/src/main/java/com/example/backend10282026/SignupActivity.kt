package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.backend10282026.data.User
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivitySignupBinding
import com.example.frontend10282026.utilities.SessionManager

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)

        binding.btnSignup.setOnClickListener {
            val email = binding.edtSignupEmail.text.toString().trim()
            val password = binding.edtSignupPassword.text.toString().trim()
            val name = binding.edtSignupUser.text.toString().trim()

            if(email.isBlank()){
                binding.edtSignupEmail.error = "Enter email"
                binding.edtSignupEmail.requestFocus()
                return@setOnClickListener
            }

            if(password.isBlank()){
                binding.edtSignupPassword.error = "Enter password"
                binding.edtSignupPassword.requestFocus()
                return@setOnClickListener
            }

            if(name.isBlank()){
                binding.edtSignupUser.error = "Enter username"
                binding.edtSignupUser.requestFocus()
                return@setOnClickListener
            }

            if(password.length < 6){
                binding.edtSignupPassword.error = "Password is too short"
                binding.edtSignupPassword.requestFocus()
                return@setOnClickListener
            }

            val user = User(
                name = name,
                email = email,
                password = password,
                role = "CUSTOMER"
            )

            dao.insertUser(user)
            Toast.makeText(this, "Successfully added User!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))

        }

    }
}