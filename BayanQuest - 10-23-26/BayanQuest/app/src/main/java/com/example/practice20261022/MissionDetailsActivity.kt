package com.example.practice20261022

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice20261022.data.Item
import com.example.practice20261022.data.Mission
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityMissionDetailsBinding
import com.example.practice20261022.databinding.ItemMissionChecklistBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import com.example.practice20261022.utilities.sendNotif
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class MissionDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMissionDetailsBinding
    private lateinit var itemAdapter: UniversalAdapter<Item, ItemMissionChecklistBinding>
    private var mission: Mission ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMissionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val missionId = intent.getLongExtra("ID", -1L)

        if (missionId != -1L){
            mission = dao.getMissionById(missionId)


            if (mission != null){
                binding.txvMissionName.text = mission!!.title
                binding.txvMissionCategory.text = mission!!.category
                binding.txvMissionXPReward.text = mission!!.xpReward.toString()
                binding.txvMissionStatus.text = mission!!.status
                binding.txvMissionDate.text = mission!!.date
                binding.txvMissionDescription.text = mission!!.description

                val max = mission!!.goal
                val progress = mission!!.progress
                val percent = if (max > 0) (progress * 100) / max else 0
                binding.txvMissionPercent.text = "$percent% Done"

                binding.btnDeleteMissionDetail.setOnClickListener {
                    dao.deleteMission(mission!!)
                    Toast.makeText(this, "Mission ${mission!!.title} has been deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MissionsActivity::class.java))
                    finish()
                }
            }
        }

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
                b.btnDeleteItem.setOnClickListener {
                    dao.deleteItem(item)
                    Snackbar.make(binding.root, "Mission ${item.id} Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            dao.insertItem(item)
                        }
                        .show()
                }

                b.btnEditItem.setOnClickListener {
                    MissionItemSheet(missionId, item.description).show(supportFragmentManager, "MissionItemSheet")
                }


            },
            onItemClick = {}
        )

        binding.rcvMissionItems.adapter = itemAdapter
        binding.rcvMissionItems.layoutManager = LinearLayoutManager(this)

        binding.btnEditDetailsMission.setOnClickListener {
            EditMissionSheet(missionId).show(supportFragmentManager, "EditMissionSheet")
        }



        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MissionsActivity::class.java))
            finish()
        }

        binding.btnMissionNext.setOnClickListener {
            MissionItemSheet(missionId, "").show(supportFragmentManager, "MissionItemSheet")
        }

        lifecycleScope.launch {
            dao.getItemByMissionId(missionId).collect { item ->
                itemAdapter.submitList(item)

                val max = item.size
                val progress =  item.count {it.isChecked}

                binding.lpiMissionProgress.max = max
                binding.lpiMissionProgress.progress = progress

                val progressPercent = if (max > 0) (progress * 100) / max else 0

                val totalCount = item.size
                val checkedCount = item.count {it.isChecked}
                val isNowCompleted = totalCount > 0 && checkedCount == totalCount


                mission?.let { currentMission ->
                    val updated = currentMission.copy(
                        goal = totalCount,
                        progress = checkedCount,
                        status = if (isNowCompleted) "Completed" else "Pending"
                    )
                    mission = updated
                    dao.updateMission(updated)
                    binding.txvMissionStatus.text = updated.status
                }

                binding.txvMissionPercent.text = "$progressPercent% Done"

                if (isNowCompleted){




                        val allMissions = dao.getMissionsListByUserId(userId)
                        val totalMissions = allMissions.size
                        val completedMissions = allMissions.filter {it.status.equals("Completed", ignoreCase = true)}

                        val completedCount = completedMissions.size
                        val goBagCompleted = allMissions.filter { it.status.equals("Completed", ignoreCase =  true) && it.category.equals("Go-Bag Supply", ignoreCase = true) }
                        val goBagCompletedCount = goBagCompleted.size

                        if (completedCount >= 1){
                            dao.unlockAchievement(userId, "First Responder")
                        }

                        if (goBagCompletedCount >= 3){
                            dao.unlockAchievement(userId, "Go-Bag Ready")
                        }

                        if (completedCount >= 4){
                            dao.unlockAchievement(userId, "Prepared Citizen")
                        }

                        if ( completedCount >= 5 && completedCount == totalMissions){
                            dao.unlockAchievement(userId, "Quest Master")
                        }

                        sendNotif("Mission Completed", "You completed ${mission!!.title}!")



                }
            }
        }




    }
}