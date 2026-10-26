package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261026.data.User
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityLoginBinding
import com.example.practice20261026.databinding.ActivitySignupBinding
import com.example.practice20261026.utilities.SessionManager

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        binding.btnSignUp.setOnClickListener {
            val username = binding.edtUsernameSignup.text.toString().trim()
            val email = binding.edtUserEmailSignup.text.toString().trim()
            val password = binding.edtPasswordSignup.text.toString().trim()

            if (username.isBlank()){
                binding.edtUsernameSignup.error = "Enter an Email"
                binding.edtUsernameSignup.requestFocus()
                return@setOnClickListener
            }
            if (email.isBlank()){
                binding.edtUserEmailSignup.error = "Enter an Email"
                binding.edtUserEmailSignup.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtUserEmailSignup.error = "Not a valid email format"
                binding.edtUserEmailSignup.requestFocus()
                return@setOnClickListener
            }
            if (password.isBlank()){
                binding.edtPasswordSignup.error = "Enter a Password"
                binding.edtPasswordSignup.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6){
                binding.edtPasswordSignup.error = "Password must be more than 6 characters"
                binding.edtPasswordSignup.requestFocus()
                return@setOnClickListener
            }

            val user = User(
                name = username,
                email = email,
                password = password,
                role = "USER"
            )

            dao.insertUser(user)
            Toast.makeText(this, "Successfully Signed Up $username", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.txvHasAccountLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}