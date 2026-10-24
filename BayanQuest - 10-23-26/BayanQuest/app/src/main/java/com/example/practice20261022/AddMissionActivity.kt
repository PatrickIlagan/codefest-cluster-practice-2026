package com.example.practice20261022

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practice20261022.data.Mission
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityAddMissionBinding
import com.example.practice20261022.utilities.SessionManager

class AddMissionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMissionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddMissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()
        val calendar = Calendar.getInstance()

        var timestamp: Long = 0L

        val category = arrayOf("Go-Bag Supply", "Household Prep", "Evacuation", "Safety Guide")
        binding.edmMissionCategory.setSimpleItems(category)

        binding.edtMissionDate.setOnClickListener {
            DatePickerDialog(
                this,
                {_, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    binding.edtMissionDate.setText("${day}/${month}/${year}")
                    timestamp = calendar.timeInMillis
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnAddMission.setOnClickListener {
            val name = binding.edtMissionName.text.toString().trim()
            val category = binding.edmMissionCategory.text.toString().trim()
            val xpReward = binding.edtXPReward.text.toString().trim()


            val description = binding.edtMissionDescription.text.toString().trim()
            val status = binding.root.findViewById<RadioButton>(binding.rdgStatus.checkedRadioButtonId)?.text?.toString() ?: "Null"

            if(name.isBlank()){
                binding.edtMissionName.error = "Enter a Name"
                binding.edtMissionName.requestFocus()
                return@setOnClickListener
            }
            if(category.isBlank()){
                binding.edtMissionName.error = "Enter a Category"
                binding.edtMissionName.requestFocus()
                return@setOnClickListener
            }
            if(xpReward.isBlank()){
                binding.edtMissionName.error = "Enter a XP Reward"
                binding.edtMissionName.requestFocus()
                return@setOnClickListener
            }
            if(description.isBlank()){
                binding.edtMissionName.error = "Enter a Description"
                binding.edtMissionName.requestFocus()
                return@setOnClickListener
            }



            val date = binding.edtMissionDate.text.toString().trim()
            if(date.isBlank()){
                binding.edtMissionName.error = "Enter a Date"
                binding.edtMissionName.requestFocus()
                return@setOnClickListener
            }
            val intXp = xpReward.toIntOrNull() ?: 0
            val newMission = Mission(
                userId = userId,
                title = name,
                category = category,
                xpReward = intXp,
                status = status,
                date = date,
                timestamp = timestamp,
                description = description,
                progress = if (status == "Completed") 1 else 0,
                goal = 1

            )

            dao.insertMission(newMission)

            Toast.makeText(this, "Succesfully Made a New Mission: $name", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MissionsActivity::class.java))
            finish()




        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MissionsActivity::class.java))
            finish()
        }
    }
}