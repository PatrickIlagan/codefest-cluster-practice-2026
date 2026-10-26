package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityLoginBinding
import com.example.practice20261026.databinding.ActivityMainBinding
import com.example.practice20261026.utilities.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        if (session.getUserID() != -1L){
            startActivity(Intent(this, MainActivity::class.java))

        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtUserEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if (email.isBlank()){
                binding.edtUserEmail.error = "Enter an Email"
                binding.edtUserEmail.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtUserEmail.error = "Not a valid email format"
                binding.edtUserEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isBlank()){
                binding.edtPassword.error = "Enter a Password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6){
                binding.edtPassword.error = "Password must be more than 6 characters"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            val user = dao.getUserByEmailAndPassword(email, password)

            if (user != null){
                session.login(user.id, user.name, user.role)
                Toast.makeText(this, "Successfuly Logged In!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        binding.txvHasAccountLogin.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}