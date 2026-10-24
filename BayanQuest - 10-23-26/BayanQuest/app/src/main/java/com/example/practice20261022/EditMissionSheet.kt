package com.example.practice20261022

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityEditMissionBinding
import com.example.practice20261022.databinding.ActivityEditResourceBinding
import com.example.practice20261022.utilities.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditMissionSheet(
    private val missionId: Long
) : BottomSheetDialogFragment(R.layout.activity_edit_mission){
    private lateinit var binding: ActivityEditMissionBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityEditMissionBinding.bind(view)

        val session = SessionManager(requireContext())
        val userId = session.getUserId()



        val calendar = Calendar.getInstance()

        var timestamp: Long = 0L



        if (missionId != -1L){
            val mission = dao.getMissionById(missionId)
            if (mission != null){
                val category = arrayOf("Go-Bag Supply", "Household Prep", "Evacuation", "Safety Guide")
                binding.edmMissionCategory.setSimpleItems(category)
                binding.edtMissionName.setText(mission.title)
                binding.edmMissionCategory.setText(mission.category, false)
                binding.edtXPReward.setText( mission.xpReward.toString())

                when (mission.status) {
                    "Completed" -> binding.rdgStatus.check(R.id.rbtCompleted)
                    "Pending" -> binding.rdgStatus.check(R.id.rbtPending)
                }
                binding.edtMissionName.setText(mission.title)
                binding.edtMissionDate.setText(mission.date)
                binding.edtMissionDescription.setText(mission.description)

                binding.edtMissionDate.setOnClickListener {
                    DatePickerDialog(
                        requireContext(),
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


                binding.btnSaveChanges.setOnClickListener {
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
                    val newMission = mission.copy(
                        userId = userId,
                        title = name,
                        category = category,
                        xpReward = intXp,
                        status = status,
                        date = date,
                        timestamp = timestamp,
                        description = description
                    )
                    dao.updateMission(newMission)
                    Toast.makeText(requireContext(), "Succesfully Made a New Mission: $name", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }

    }
}