package com.example.practice20261022

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261022.databinding.ActivityEditMissionBinding
import com.example.practice20261022.databinding.ActivityEditResourceBinding
import com.example.practice20261022.databinding.ActivityResourcesBinding
import com.example.practice20261022.utilities.SessionManager

class EditResourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditResourceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}