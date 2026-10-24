package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.Location
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityLocationEntriesBinding
import com.example.myapplication.databinding.LocationEntryItemBinding
import com.example.myapplication.utilities.UniversalAdapter
import com.example.myapplication.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LocationEntriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLocationEntriesBinding
    private var rawLocations: List<Location> = emptyList()
    private lateinit var adapter: UniversalAdapter<Location, LocationEntryItemBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLocationEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddLocationActivity::class.java))
            finish()
        }

        adapter = UniversalAdapter(
            inflate = LocationEntryItemBinding::inflate,
            bind = { itemBinding, location ->
                itemBinding.txvName.text = location.name
                itemBinding.txvCategory.text = location.category
                itemBinding.txvDate.text = "${location.startSched} - ${location.endSched}"

                itemBinding.btnDelete2.setOnClickListener {
                    dao.deleteLocation(location)

                    Snackbar.make(binding.root, "Deleted Location ID: ${location.id}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO"){
                            dao.insertLocation(location)
                        }
                        .show()
                }
            },
            onItemClick = { item ->
                val intent = Intent(this, DetailsLocationActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvLocationEntries.adapter = adapter
        binding.rcvLocationEntries.layoutManager = LinearLayoutManager(this)
        binding.rcvLocationEntries.isNestedScrollingEnabled = false

        binding.edtSearch.doAfterTextChanged { filter() }
        binding.cpgCategory.setOnCheckedStateChangeListener { _, _ -> filter() }

        lifecycleScope.launch {
            dao.getAllLocations().collect { locations ->
                rawLocations = locations
                filter()
            }
        }

        setupBottomNav(
            binding.bottomNav,
            currentItemId = R.id.menu_locations,
        )

    }

    private fun filter() {
        val q = binding.edtSearch.text.toString().trim()
        val checkedChip = binding.cpgCategory.findViewById<Chip>(binding.cpgCategory.checkedChipId)?.text?.toString() ?: "All"

        val filtered = rawLocations.filter { item ->
            val matchesCategory = checkedChip == "All" || item.category.equals(checkedChip.toString(), ignoreCase = true)
            val matchesSearch = q.isEmpty() || item.name.contains(q, ignoreCase = true) || item.category.contains(q, ignoreCase = true) || item.address.contains(q, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        val sorted = filtered.sortedByDescending { it.timestamp }
        adapter.submitList(sorted)
    }
}
