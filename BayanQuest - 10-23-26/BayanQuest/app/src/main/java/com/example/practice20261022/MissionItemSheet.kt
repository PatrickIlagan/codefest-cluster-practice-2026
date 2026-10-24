package com.example.practice20261022

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice20261022.data.Item
import com.example.practice20261022.data.Mission
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityMissionItemBinding
import com.example.practice20261022.databinding.ItemMissionChecklistBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class MissionItemSheet(
    private val missionId: Long,
    private val itemEdit: String = ""
) : BottomSheetDialogFragment(R.layout.activity_mission_item){
    private lateinit var binding: ActivityMissionItemBinding
    private lateinit var itemAdapter: UniversalAdapter<Item, ItemMissionChecklistBinding>
    private var mission: Mission? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityMissionItemBinding.bind(view)

        val session = SessionManager(requireContext())
        val userId = session.getUserId()

         mission = dao.getMissionById(missionId)


        var itemToEdit: Item? = null
        binding.edtEditItem.setText(itemEdit)
        if (mission != null){

            itemAdapter = UniversalAdapter(
                inflate = ItemMissionChecklistBinding::inflate,
                bind = {b, item ->


                    b.chbMissionItem.text = item.description
                    b.chbMissionItem.isChecked = item.isChecked

                    b.chbMissionItem.setOnClickListener {
                        val isChecked = b.chbMissionItem.isChecked


                        val newItem = item.copy(
                            isChecked = isChecked
                        )
                        dao.updateItem(newItem)

                    }

                    b.btnEditItem.setOnClickListener {
                        itemToEdit = item
                        binding.edtEditItem.setText(item.description)
                    }

                    b.btnDeleteItem.setOnClickListener {
                        dao.deleteItem(item)
                        Toast.makeText(requireContext(), "Deleted Item #${item.id} Successfully", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                },
                onItemClick = {

                }
            )

        }
        binding.rcvMissionItems.adapter = itemAdapter
        binding.rcvMissionItems.layoutManager = LinearLayoutManager(requireContext())
        binding.btnAddItem.setOnClickListener {
            val description = binding.edtAddItem.text.toString().trim()

            val item = Item(
                missionId = missionId,
                description = description,
                isChecked = false
            )

            dao.insertItem(item)
            Toast.makeText(requireContext(), "Sucessfuly Created new Item: ${item.id}", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveChangesItem.setOnClickListener {
            val description = binding.edtEditItem.text.toString().trim()

            if (description.isBlank()) {
                binding.edtEditItem.error = "Enter description"
                binding.edtEditItem.requestFocus()
                return@setOnClickListener
            }
            val currentTarget = itemToEdit ?: return@setOnClickListener

            val item = currentTarget.copy(
                description = description,
                isChecked = false
            )
            dao.updateItem(item)


            itemToEdit = null
            Toast.makeText(requireContext(), "Sucessfuly Created new Item: ${item.id}", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            dao.getItemByMissionId(missionId).collect { item ->
                itemAdapter.submitList(item)

                val totalCount = item.size
                val checkedCount = item.count {it.isChecked}

                mission?.let { currentMission ->
                    val updated = currentMission.copy(
                        goal = totalCount,
                        progress = checkedCount,
                        status = if (totalCount > 0 && checkedCount == totalCount) "Completed" else "Pending"
                    )
                    mission = updated
                    dao.updateMission(updated)
                }



            }
        }


    }
}