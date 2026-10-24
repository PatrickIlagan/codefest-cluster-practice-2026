package com.example.practice20261022

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practice20261022.data.Resource
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityAddResourceBinding
import com.example.practice20261022.utilities.SessionManager
import java.util.Calendar

class AddResourceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddResourceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddResourceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()
        val calendar = Calendar.getInstance()

        var timestamp: Long = 0L

        val category = arrayOf("Relief Hub", "Emergency Hotline", "Health Facility", "Evacuation Center")
        binding.edmResourceCategory.setSimpleItems(category)

        binding.edtResourceTimeStart.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.edtResourceTimeStart.setText(String.format("%02d:%02d", hour, minute))
                    timestamp = calendar.timeInMillis
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.edtResourceTimeEnd.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.edtResourceTimeEnd.setText(String.format("%02d:%02d", hour, minute))
                    timestamp = calendar.timeInMillis
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnAddResource.setOnClickListener {
            val name = binding.edtResourceName.text.toString().trim()
            val category = binding.edmResourceCategory.text.toString().trim()
            val contactNumber = binding.edtContact.text.toString().trim()
            val address = binding.edtResourceAddress.text.toString().trim()

            val remarks = binding.edtResrouceRemarks.text.toString().trim()


            if(name.isBlank()){
                binding.edtResourceName.error = "Enter a Name"
                binding.edtResourceName.requestFocus()
                return@setOnClickListener
            }
            if(category.isBlank()){
                binding.edmResourceCategory.error = "Enter a Category"
                binding.edmResourceCategory.requestFocus()
                return@setOnClickListener
            }
            if(contactNumber.isBlank()){
                binding.edtContact.error = "Enter a Contact Number"
                binding.edtContact.requestFocus()
                return@setOnClickListener
            }
            if(remarks.isBlank()){
                binding.edtResrouceRemarks.error = "Enter a Description"
                binding.edtResrouceRemarks.requestFocus()
                return@setOnClickListener
            }
            if(address.isBlank()){
                binding.edtResourceAddress.error = "Enter Address"
                binding.edtResourceAddress.requestFocus()
                return@setOnClickListener
            }
            if(remarks.isBlank()){
                binding.edtResrouceRemarks.error = "Enter Remarks"
                binding.edtResrouceRemarks.requestFocus()
                return@setOnClickListener
            }



            val startTime = binding.edtResourceTimeStart.text.toString().trim()
            val endTime = binding.edtResourceTimeEnd.text.toString().trim()
            if(startTime.isBlank()){
                binding.edtResourceTimeStart.error = "Enter Start Time"
                binding.edtResourceTimeStart.requestFocus()
                return@setOnClickListener
            }
            if(endTime.isBlank()){
                binding.edtResourceTimeEnd.error = "Enter End Time"
                binding.edtResourceTimeEnd.requestFocus()
                return@setOnClickListener
            }

            val newResource = Resource(
                userId = userId,
                name = name,
                category = category,
                contactNumber = contactNumber,
                address = address,
                start = startTime,
                end = endTime,
                remarks = remarks
            )

            dao.insertResource(newResource)

            Toast.makeText(this, "Succesfully Made a New Resource: $name", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ResourcesActivity::class.java))
            finish()

        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ResourcesActivity::class.java))
            finish()
        }


    }
}