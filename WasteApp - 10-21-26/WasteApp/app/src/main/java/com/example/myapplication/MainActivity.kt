package com.example.myapplication

import android.content.Intent
import android.os.Build
import com.example.myapplication.data.Location
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.Entry
import com.example.myapplication.data.User
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityLocationEntriesBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.LocationEntryItemBinding
import com.example.myapplication.databinding.WasteEntryItemBinding
import com.example.myapplication.utilities.SessionManager
import com.example.myapplication.utilities.UniversalAdapter
import com.example.myapplication.utilities.setupBottomNav
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var wasteAdapter: UniversalAdapter<Entry, WasteEntryItemBinding>
    private lateinit var locationAdapter: UniversalAdapter<Location, LocationEntryItemBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"),1)
        }

        val session = SessionManager(this)
        val userId = session.getUserId()

        if (userId == -1L) {
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
            return
        }

        binding.textView.text = "Welcome, ${session.getUserName()}"

        locationAdapter = UniversalAdapter(
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

        binding.rcvRecycleLocations.adapter = locationAdapter
        binding.rcvRecycleLocations.layoutManager = LinearLayoutManager(this)
        binding.rcvRecycleLocations.isNestedScrollingEnabled = false

        wasteAdapter = UniversalAdapter(
            inflate = WasteEntryItemBinding::inflate,
            bind = { itemBinding, entry ->
                itemBinding.txvName.text = entry.name
                itemBinding.txvCategory.text = entry.category
                itemBinding.txvQuantity.text = entry.quantity.toString()+ "x"
                itemBinding.txvDate.text = entry.date

                itemBinding.btnDelete2.setOnClickListener {
                    dao.deleteEntry(entry)
                    Snackbar.make(binding.root, "Deleted Entry ID: ${entry.id}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO"){
                            dao.insertEntry(entry)
                        }
                        .show()
                }
            },
            onItemClick = { entry ->
                val intent = Intent(this, DetailsWasteActivity::class.java).apply{
                    putExtra("WASTE_ID", entry.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvWasteEntries.adapter = wasteAdapter
        binding.rcvWasteEntries.layoutManager = LinearLayoutManager(this)
        binding.rcvWasteEntries.isNestedScrollingEnabled = false

        setupBottomNav(
            binding.bottomNav,
            currentItemId = R.id.menu_home,
        )

        binding.btnWasteEntries.setOnClickListener {
            startActivity(Intent(this, WasteEntriesActivity::class.java))
            finish()
        }

        binding.btnRecycleLocations.setOnClickListener {
            startActivity(Intent(this, LocationEntriesActivity::class.java))
            finish()
        }

        binding.btnWasteGuide.setOnClickListener {
            startActivity(Intent(this, WasteEntriesActivity::class.java))
            finish()
        }

        binding.btnRecyclingLogs.setOnClickListener {
            startActivity(Intent(this, WasteEntriesActivity::class.java))
            finish()
        }

        binding.btnRecyclingDirectory.setOnClickListener {
            startActivity(Intent(this, LocationEntriesActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllEntriesByUser(userId).collect { entries ->
                val totalEntries = entries.size
                val totalRecycleEntries = entries.count { it.category.equals("Recyclable", ignoreCase = true) }
                val totalBiodegradable = entries.count { it.category.equals("Biodegradable", ignoreCase = true) }
                val totalHazardous = entries.count { it.category.equals("Special Waste", ignoreCase = true) }
                val totalResidual = entries.count { it.category.equals("Residual", ignoreCase = true) }

                binding.txvTotalEntries.text = totalEntries.toString()
                binding.txvTotalRecycleEntries.text = totalRecycleEntries.toString()
                binding.txvTotalRecycle.text = totalRecycleEntries.toString()
                binding.txvTotalBiodegradable.text = totalBiodegradable.toString()
                binding.txvTotalResidual.text = totalResidual.toString()
                binding.txvTotalHazardous.text = totalHazardous.toString()

            }
        }

        lifecycleScope.launch {
            dao.getAllEntriesByUser(userId).collect { entries ->
                val sortedEntries = entries.sortedByDescending { it.timestamp }.take(4)
                wasteAdapter.submitList(sortedEntries)
            }
        }

        lifecycleScope.launch {
            dao.getAllLocations().collect { locations ->
                val sortedEntries = locations.sortedByDescending { it.name }.take(4)
                locationAdapter.submitList(sortedEntries)
            }
        }

    }
}