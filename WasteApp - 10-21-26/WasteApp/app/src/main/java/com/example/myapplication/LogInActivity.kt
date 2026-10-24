package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityLogInBinding
import com.example.myapplication.utilities.SessionManager

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        binding.txvSignUp.setOnClickListener {
            // Navigate to SignUpActivity
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        binding.btnLogIn.setOnClickListener {
            val email = binding.edtUsernameEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if(email.isBlank()){
                binding.edtUsernameEmail.error = "Enter an Email"
                binding.edtUsernameEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtUsernameEmail.error = "Email Invalid"
                binding.edtUsernameEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isBlank()){
                binding.edtPassword.error = "Enter a password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            val user = dao.loginUser(email, password)
            
            if (user == null){
                binding.edtUsernameEmail.error = "User not Found!"
                binding.edtUsernameEmail.requestFocus()
                Toast.makeText(this, "User not Found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            session.login(user.id, user.name, user.role)
            Toast.makeText(this, "Successfully logged in!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}