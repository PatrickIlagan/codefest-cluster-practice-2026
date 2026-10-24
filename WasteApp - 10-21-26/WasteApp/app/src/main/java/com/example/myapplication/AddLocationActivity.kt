package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.Location
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityAddLocationBinding
import java.util.Calendar

class AddLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calendar = Calendar.getInstance()


        val categories = arrayOf("Biodegradable", "Recyclable", "Residual", "Special Waste")
        binding.edmMaterialTypes.setSimpleItems(categories)

        binding.edtStart.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minutes ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minutes)
                    binding.edtStart.setText(String.format("%02d:%02d", hour, minutes))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.edtEnd.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minutes ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minutes)
                    binding.edtEnd.setText(String.format("%02d:%02d", hour, minutes))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val timestamp = calendar.timeInMillis

        binding.btnAddLocation.setOnClickListener {
            val name = binding.edLocationName.text.toString().trim()
            val selectedCategory = binding.edmMaterialTypes.text.toString().trim()
            val startSched = binding.edtStart.text.toString().trim()
            val endSched = binding.edtEnd.text.toString().trim()
            val address = binding.edtAddress.text.toString().trim()
            val remarks = binding.edtWasteRemarks.text.toString().trim()

            if (name.isBlank()){
                binding.edLocationName.error = "Please Enter a Location Name"
                binding.edLocationName.requestFocus()
                return@setOnClickListener
            }
            if (selectedCategory.isBlank()){
                binding.edmMaterialTypes.error = "Please Select a Category"
                binding.edmMaterialTypes.requestFocus()
                return@setOnClickListener
            }
            if (startSched.isBlank()){
                binding.edtStart.error = "Please Select a Start Time"
                binding.edtStart.requestFocus()
                return@setOnClickListener
            }
            if (endSched.isBlank()){
                binding.edtEnd.error = "Please Select a End Time"
                binding.edtEnd.requestFocus()
                return@setOnClickListener
            }
            if(address.isBlank()){
                binding.edtAddress.error = "Please Enter an Address"
                binding.edtAddress.requestFocus()
                return@setOnClickListener
            }
            if(remarks.isBlank()){
                binding.edtWasteRemarks.error = "Please Enter some Remarks"
                binding.edtWasteRemarks.requestFocus()
                return@setOnClickListener
            }

            val location = Location(
                name = name,
                category = selectedCategory,
                address = address,
                remarks = remarks,
                startSched = startSched,
                endSched = endSched,
                timestamp = timestamp
            )



            dao.insertLocation(location)
            val intent = Intent(this, LocationEntriesActivity::class.java)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Location has been inserted!", Toast.LENGTH_SHORT).show()
        }
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LocationEntriesActivity::class.java))
            finish()
        }
    }
}