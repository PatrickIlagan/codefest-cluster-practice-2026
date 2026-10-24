package com.example.practice20261022

import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.practice20261022.data.Resource
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityEditResourceBinding
import com.example.practice20261022.utilities.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditResourceSheet(
    private val resourceId: Long
) : BottomSheetDialogFragment(R.layout.activity_edit_resource) {
    private lateinit var binding: ActivityEditResourceBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityEditResourceBinding.bind(view)

        val session = SessionManager(requireContext())
        val userId = session.getUserId()

        val calendar = Calendar.getInstance()

        val resource = dao.getResourceByID(resourceId)

        var timestamp: Long = 0L

        if (resource != null){
            val category = arrayOf("Relief Hub", "Emergency Hotline", "Health Facility", "Evacuation Center")
            binding.edmResourceCategory.setSimpleItems(category)
            binding.edtResourceName.setText(resource.name)
            binding.edmResourceCategory.setText(resource.category, false)
            binding.edtContact.setText(resource.contactNumber)
            binding.edtResourceAddress.setText(resource.address)
            binding.edtResourceTimeStart.setText(resource.start)
            binding.edtResourceTimeEnd.setText(resource.end)
            binding.edtResrouceRemarks.setText(resource.remarks)
        binding.edtResourceTimeStart.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
                    calendar.set(java.util.Calendar.MINUTE, minute)
                    binding.edtResourceTimeStart.setText(String.format("%02d:%02d", hour, minute))
                    timestamp = calendar.timeInMillis
                },
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        binding.edtResourceTimeEnd.setOnClickListener {
            TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
                    calendar.set(java.util.Calendar.MINUTE, minute)
                    binding.edtResourceTimeEnd.setText(String.format("%02d:%02d", hour, minute))
                    timestamp = calendar.timeInMillis
                },
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE),
                true
            ).show()
        }

        binding.btnSaveChanges.setOnClickListener {
            val name = binding.edtResourceName.text.toString().trim()
            val category = binding.edmResourceCategory.text.toString().trim()
            val contactNumber = binding.edtContact.text.toString().trim()
            val address = binding.edtResourceAddress.text.toString().trim()

            val remarks = binding.edtResrouceRemarks.text.toString().trim()


            if (name.isBlank()) {
                binding.edtResourceName.error = "Enter a Name"
                binding.edtResourceName.requestFocus()
                return@setOnClickListener
            }
            if (category.isBlank()) {
                binding.edmResourceCategory.error = "Enter a Category"
                binding.edmResourceCategory.requestFocus()
                return@setOnClickListener
            }
            if (contactNumber.isBlank()) {
                binding.edtContact.error = "Enter a Contact Number"
                binding.edtContact.requestFocus()
                return@setOnClickListener
            }
            if (remarks.isBlank()) {
                binding.edtResrouceRemarks.error = "Enter a Description"
                binding.edtResrouceRemarks.requestFocus()
                return@setOnClickListener
            }
            if (address.isBlank()) {
                binding.edtResourceAddress.error = "Enter Address"
                binding.edtResourceAddress.requestFocus()
                return@setOnClickListener
            }
            if (remarks.isBlank()) {
                binding.edtResrouceRemarks.error = "Enter Remarks"
                binding.edtResrouceRemarks.requestFocus()
                return@setOnClickListener
            }


            val startTime = binding.edtResourceTimeStart.text.toString().trim()
            val endTime = binding.edtResourceTimeEnd.text.toString().trim()
            if (startTime.isBlank()) {
                binding.edtResourceTimeStart.error = "Enter Start Time"
                binding.edtResourceTimeStart.requestFocus()
                return@setOnClickListener
            }
            if (endTime.isBlank()) {
                binding.edtResourceTimeEnd.error = "Enter End Time"
                binding.edtResourceTimeEnd.requestFocus()
                return@setOnClickListener
            }

            val newResource = resource.copy(
                userId = userId,
                name = name,
                category = category,
                contactNumber = contactNumber,
                address = address,
                start = startTime,
                end = endTime,
                remarks = remarks
            )

            dao.updateResource(newResource)
            Toast.makeText(requireContext(), "Succesfully Made a New Resource: $name", Toast.LENGTH_SHORT).show()
            dismiss()

        }
        }

    }
}