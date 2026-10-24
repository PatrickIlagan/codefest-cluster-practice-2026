package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.Entry
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityAddWasteBinding
import com.example.myapplication.utilities.SessionManager

class AddWasteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddWasteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val session = SessionManager(this)
        val userId = session.getUserId()
        val calendar = Calendar.getInstance()
        binding = ActivityAddWasteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categories = arrayOf("Biodegradable", "Recyclable", "Residual", "Special Waste")
        binding.edmCategory.setSimpleItems(categories)

        var timestamp = System.currentTimeMillis()

        binding.edtWasteDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    timestamp = calendar.timeInMillis
                    binding.edtWasteDate.setText("${month + 1}/${day}/${year}")
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, WasteEntriesActivity::class.java))
            finish()
        }

        binding.btnAddWaste.setOnClickListener {
            val name = binding.edtWasteName.text.toString().trim()
            val qty = binding.edtWasteQuantity.text.toString().trim()
            val date = binding.edtWasteDate.text.toString().trim()
            val method = binding.edtWasteDisposalMethod.text.toString().trim()
            val remarks = binding.edtWasteRemarks.text.toString().trim()
            val selectedCategory = binding.edmCategory.text.toString().trim()

            if (name.isBlank()){
                binding.edtWasteName.error = "Please Enter A Name"
                binding.edtWasteName.requestFocus()
                return@setOnClickListener
            }
            if (qty.isBlank()){
                binding.edtWasteQuantity.error = "Please Enter Quantity"
                binding.edtWasteQuantity.requestFocus()
                return@setOnClickListener
            }
            if (date.isBlank()){
                binding.edtWasteDate.error = "Please Select a Date"
                binding.edtWasteDate.requestFocus()
                return@setOnClickListener
            }
            if (method.isBlank()){
                binding.edtWasteDisposalMethod.error = "Please Enter a Disposal Method"
                binding.edtWasteDisposalMethod.requestFocus()
                return@setOnClickListener
            }
            if (remarks.isBlank()){
                binding.edtWasteRemarks.error = "Please Enter Remarks"
                binding.edtWasteRemarks.requestFocus()
                return@setOnClickListener
            }
            if (selectedCategory.isBlank()){
                binding.edmCategory.error = "Please Enter Category"
                binding.edmCategory.requestFocus()
                return@setOnClickListener
            }






            val quantity = qty.toIntOrNull() ?: 0

            if(userId != -1L && quantity != 0) {
                val wasteItem = Entry(
                    userId = userId,
                    name = name,
                    category = selectedCategory,
                    instruction = method,
                    quantity = quantity,
                    date = date,
                    timestamp = timestamp,
                    description = remarks
                )

                dao.insertEntry(wasteItem)
            }





            val intent = Intent(this, WasteEntriesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}