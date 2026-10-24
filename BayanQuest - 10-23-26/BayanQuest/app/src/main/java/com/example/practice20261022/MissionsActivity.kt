package com.example.practice20261022

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice20261022.data.Mission
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityMissionsBinding
import com.example.practice20261022.databinding.ItemMissionBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import com.example.practice20261022.utilities.setupBottomNav

class MissionsActivity : AppCompatActivity() {
    private lateinit var missionAdapter: UniversalAdapter<Mission, ItemMissionBinding>
    private lateinit var binding: ActivityMissionsBinding
    private var isAscending: Boolean = false

    private var rawMissions: List<Mission> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        missionAdapter = UniversalAdapter(
            inflate = ItemMissionBinding::inflate,
            bind = {b, item ->
                b.txvMissionItemName.text = item.title
                b.txvMissionItemCategory.text = item.category
                b.txvMissionItemStatus.text = item.status
                b.txvMissionItemDate.text = item.date

                b.btnEditMission.setOnClickListener {
                    EditMissionSheet(item.id).show(supportFragmentManager, "EditMissionSheet")
                }

                b.btnDeleteMission.setOnClickListener {
                    dao.deleteMission(item)

                    Snackbar.make(binding.root, "Mission ${item.title} Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            dao.insertMission(item)
                        }
                        .show()

                }

                if (item.status.equals("Completed", ignoreCase = true)){
                     b.root.alpha = 0.5f
                    b.root.strokeWidth = 6
                    b.root.strokeColor = "#93D645".toColorInt()
                } else {
                    b.root.alpha = 1f
                    b.root.strokeWidth = 6
                    b.root.strokeColor = "#BA2D2D".toColorInt()
                }
            },
            onItemClick = { item ->
                val intent = Intent(this, MissionDetailsActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvMissions.adapter = missionAdapter
        binding.rcvMissions.layoutManager = LinearLayoutManager(this)
        binding.rcvMissions.isNestedScrollingEnabled=false

        binding.edtSearch.doAfterTextChanged { filter() }
        binding.cpgMissionCategory.setOnCheckedStateChangeListener { _, _  -> filter() }
        binding.btnDateSort.setOnClickListener {
            isAscending = !isAscending
            filter()
        }

        binding.fabAdd.setOnClickListener {

            startActivity(Intent(this, AddMissionActivity::class.java))
            finish()

        }

        lifecycleScope.launch {
            dao.getAllMissionsByUserId(userId).collect { missions ->
                rawMissions = missions
                filter()
            }
        }

        binding.btnRank.setOnClickListener {
            startActivity(Intent(this, RankActivity::class.java))
            finish()
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        setupBottomNav(binding.bottomNav, R.id.nav_mission)
        lifecycleScope.launch {
            dao.getAllMissionsByUserId(userId).collect { missions ->
                val currentXP = missions.filter { it.status == "Completed" }.sumOf { it.xpReward }

                binding.txvXP.text = currentXP.toString()

                if (currentXP <= 199){
                    binding.btnRank.setImageResource(R.drawable.rank_1_scout)
                } else if (currentXP <= 399){
                    binding.btnRank.setImageResource(R.drawable.rank_2_responder)
                } else if (currentXP <= 599){
                    binding.btnRank.setImageResource(R.drawable.rank_3_vanguard)
                }else {
                    binding.btnRank.setImageResource(R.drawable.rank_4_hero)
                }
            }
        }
    }
    private fun filter(){
        var filtered = rawMissions
        val query = binding.edtSearch.text.toString().trim()
        val selectedCategory = findViewById<Chip>(binding.cpgMissionCategory.checkedChipId)?.text?.toString() ?: "All"

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        if (selectedCategory != "All"){
            filtered = filtered.filter {it.category.contains(selectedCategory, ignoreCase = true)}
        }

        filtered = if (isAscending) {
            filtered.sortedBy { it.timestamp }
        } else {
            filtered.sortedByDescending { it.timestamp }
        }

        missionAdapter.submitList(filtered)
    }
}