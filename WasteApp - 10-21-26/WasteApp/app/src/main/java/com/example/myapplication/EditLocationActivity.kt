package com.example.myapplication

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityEditLocationBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class EditLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val calendar = Calendar.getInstance()
        val locationId = intent.getLongExtra("ID", -1L)
        val location = dao.getLocationById(locationId)
        if (locationId != -1L){
            binding.edLocationName.setText(location.name)
            binding.edmMaterialTypes.setText(location.category, false)
            binding.edtStart.setText(location.startSched)
            binding.edtEnd.setText(location.endSched)
            binding.edtAddress.setText(location.address)
            binding.edtWasteRemarks.setText(location.remarks)
        }

        val categories = arrayOf("Biodegradable", "Recyclable", "Residual", "Special Waste")
        binding.edmMaterialTypes.setSimpleItems(categories)

        binding.edtStart.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.edtStart.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        binding.edtEnd.setOnClickListener {
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    binding.edtEnd.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        val timestamp = calendar.timeInMillis

        binding.btnSaveLocation.setOnClickListener {
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

            val updatedLocation = location.copy(
                name = name,
                category = selectedCategory,
                address = address,
                remarks = remarks,
                startSched = startSched,
                endSched = endSched,
                timestamp = timestamp
            )

            dao.updateLocation(updatedLocation)
            Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DetailsLocationActivity::class.java).apply {
                putExtra("ID", location.id)
            }
            startActivity(intent)
            finish()

        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, DetailsLocationActivity::class.java).apply {
                putExtra("ID", locationId)
            }
            startActivity(intent)
            finish()
        }

        binding.btnDelete4.setOnClickListener {
            dao.deleteLocation(location)
            Snackbar.make(binding.root, "Deleted Location!", Snackbar.LENGTH_LONG)
            .setAction("UNDO"){
                dao.insertLocation(location)
            }
            .show()

            startActivity(Intent(this, LocationEntriesActivity::class.java))
            finish()
        }
    }
}