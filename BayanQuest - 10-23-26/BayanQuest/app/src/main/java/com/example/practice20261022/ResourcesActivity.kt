package com.example.practice20261022

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice20261022.data.Resource
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityResourcesBinding
import com.example.practice20261022.databinding.ItemResourceBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import com.example.practice20261022.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.text.equals

class ResourcesActivity : AppCompatActivity() {
    private lateinit var resourceAdapter: UniversalAdapter<Resource, ItemResourceBinding>
    private lateinit var binding: ActivityResourcesBinding

    private var isAscending: Boolean = false


    private var rawResources: List<Resource> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        resourceAdapter = UniversalAdapter(
            inflate = ItemResourceBinding::inflate,
            bind = {b, item ->
                b.txvResourceName.text = item.name
                b.txvResourceCategory.text = item.category
                b.txvResourceContact.text = item.contactNumber
                b.txvResourceSchedule.text = "${item.start} - ${item.end}"

                b.btnEditResource.setOnClickListener {
                    EditResourceSheet(item.id).show(supportFragmentManager, "EditResourceSheet")
                }

                b.btnDeleteResource.setOnClickListener {
                    dao.deleteResource(item)

                    Snackbar.make(binding.root, "Resource ${item.name} Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            dao.insertResource(item)
                        }
                        .show()

                }
            },
            onItemClick = { item ->
                val intent = Intent(this, ResourceDetailsActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvResources.adapter = resourceAdapter
        binding.rcvResources.layoutManager = LinearLayoutManager(this)
        binding.rcvResources.isNestedScrollingEnabled=false

        binding.edtSearch.doAfterTextChanged { filter() }
        binding.cpgResourceCategory.setOnCheckedStateChangeListener { _, _  -> filter() }
        binding.btnDateSort.setOnClickListener {
            isAscending = !isAscending
            filter()
        }

        binding.fabAdd.setOnClickListener {

            startActivity(Intent(this, AddResourceActivity::class.java))
            finish()

        }

        lifecycleScope.launch {
            dao.getAllResourcesByUserId(userId).collect { res ->
                rawResources = res
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
        setupBottomNav(binding.bottomNav, R.id.nav_resource)
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
        var filtered = rawResources
        val query = binding.edtSearch.text.toString().trim()
        val selectedCategory = findViewById<Chip>(binding.cpgResourceCategory.checkedChipId)?.text?.toString() ?: "All"

        if (query.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        if (selectedCategory != "All"){
            filtered = filtered.filter {it.category.contains(selectedCategory, ignoreCase = true)}
        }

        filtered = if (isAscending) {
            filtered.sortedBy { it.start }
        } else {
            filtered.sortedByDescending { it.end }
        }

        resourceAdapter.submitList(filtered)
    }
}