package com.example.practice20261022

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityLogInBinding
import com.example.practice20261022.utilities.SessionManager
import kotlinx.coroutines.launch

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)

        if (session.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
        }

        binding.btnLogIn.setOnClickListener {
            val email = binding.edtUsernameEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            if(email.isBlank()){
                binding.edtUsernameEmail.error = "Enter an Email"
                binding.edtUsernameEmail.requestFocus()
                return@setOnClickListener
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtUsernameEmail.error = "Email Invalid Format"
                binding.edtUsernameEmail.requestFocus()
                return@setOnClickListener
            }
            if(password.isBlank()){
                binding.edtPassword.error = "Enter a Password"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            val user = dao.loginUser(email, password)

            if (user == null){
                binding.edtUsernameEmail.error = "User does not exist."
                binding.edtUsernameEmail.requestFocus()
                return@setOnClickListener
            } else {
                session.login(user.id, user.name, user.rank)
            }

            startActivity(Intent(this, MainActivity::class.java))

        }

        binding.txvNoAccount.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))

        }




    }
}