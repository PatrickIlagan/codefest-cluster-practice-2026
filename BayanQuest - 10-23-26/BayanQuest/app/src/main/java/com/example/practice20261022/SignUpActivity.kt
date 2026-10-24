package com.example.practice20261022

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practice20261022.data.User
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivitySignUpBinding
import com.example.practice20261022.utilities.SessionManager

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)


        binding.btnSignUp.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if(username.isBlank()){
                binding.edtUsername.error = "Enter a Username"
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }
            if(email.isBlank()){
                binding.edtEmail.error = "Enter an Email"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmail.error = "Email Invalid Format"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isBlank()){
                binding.edtPassword.error = "Enter a Password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

           val newUser = User(
               name = username,
               email = email,
               password = password,
               rank = "Citizen Scout"
           )

            dao.insertUser(newUser)
            Toast.makeText(this, "Sign Up Complete", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
    }
}