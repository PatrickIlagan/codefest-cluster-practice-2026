package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityLoginBinding
import com.example.frontend10282026.utilities.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sessionManager = SessionManager(this)

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if(email.isBlank()){
                binding.edtEmail.error = "Enter Email"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmail.error = "Email Invalid"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }

            if(password.isBlank()){
                binding.edtPassword.error = "Enter Password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }


            val user = dao.loginUser(email, password)
            if (user != null){
                sessionManager.login(user.id, user.name, user.role)
                startActivity(Intent(this, MainActivity::class.java))

            } else {
                binding.edtEmail.error = "USER NOT FOUND"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }


        }

        binding.txvHasNoAccount.setOnClickListener{
            startActivity(Intent(this, SignupActivity::class.java))
        }

    }
}