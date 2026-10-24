package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.User
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnSignUp.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (username.isBlank()){
                binding.edtUsername.error = "Enter a Username"
                binding.edtUsername.requestFocus()
                return@setOnClickListener
            }
            if(email.isBlank()){
                binding.edtEmail.error = "Enter an Email"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmail.error = "Email Invalid"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isBlank()){
                binding.edtPassword.error = "Enter a password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            val newUser = User(
                name = username,
                email = email,
                password = password,
                role = "MEMBER"
            )

            dao.insertUser(newUser)
            Toast.makeText(this, "Successfully made a new user", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }


    }
}
