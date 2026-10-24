package com.example.myapplication

import android.content.Intent
import com.example.myapplication.R
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.Entry
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityWasteEntriesBinding
import com.example.myapplication.databinding.WasteEntryItemBinding
import com.example.myapplication.utilities.SessionManager
import com.example.myapplication.utilities.UniversalAdapter
import com.example.myapplication.utilities.sendNotif
import com.example.myapplication.utilities.setupBottomNav
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class WasteEntriesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWasteEntriesBinding
    private var rawEntries: List<Entry> = emptyList()
    private lateinit var adapter: UniversalAdapter<Entry, WasteEntryItemBinding>
    private var isAscending: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWasteEntriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()


        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, AddWasteActivity::class.java))
            finish()
        }




        adapter = UniversalAdapter(
            inflate = WasteEntryItemBinding::inflate,
            bind = { itemBinding, entry ->
                itemBinding.txvName.text = entry.name
                itemBinding.txvCategory.text = entry.category
                itemBinding.txvQuantity.text = entry.quantity.toString() + "x"
                itemBinding.txvDate.text = entry.date

                itemBinding.btnDelete2.setOnClickListener {
                    dao.deleteEntry(entry)



                    Snackbar.make(binding.root, "Deleted ${entry.name}", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            dao.insertEntry(entry)
                        }
                        .show()

                }

            },
            onItemClick = { item ->
                val intent = Intent(this, DetailsWasteActivity::class.java).apply {
                    putExtra("WASTE_ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvWasteEntries.adapter = adapter

        binding.rcvWasteEntries.layoutManager = LinearLayoutManager(this)

        binding.rcvWasteEntries.isNestedScrollingEnabled = false














        binding.edtSearch.doAfterTextChanged { filter() }

        binding.cpgCategory.setOnCheckedStateChangeListener { _, _ -> filter() }

        binding.btnDateFilter.setOnClickListener {
            isAscending = !isAscending

            val targetColor = if (isAscending) R.color.primary else R.color.primary_dark

            binding.btnDateFilter.backgroundTintList = ContextCompat.getColorStateList(this, targetColor)
            (binding.btnDateFilter as MaterialButton).setIconTintResource(R.color.primary)


            filter()
        }
        
        lifecycleScope.launch {
            dao.getAllEntriesByUser(userId).collect { entries ->
                rawEntries = entries
                filter()
            }
        }

        setupBottomNav(
            binding.bottomNav,
            currentItemId = R.id.menu_wastes,
        )
    }

    private fun filter() {
        val q = binding.edtSearch.text.toString().trim()
        val checkedChip = binding.cpgCategory.findViewById<Chip>(binding.cpgCategory.checkedChipId)?.text?.toString() ?: "All"

        val filtered = rawEntries.filter { item ->
            val matchesCategory = checkedChip == "All" || item.category.equals(checkedChip.toString(), ignoreCase = true)
            val matchesSearch = q.isEmpty() || item.name.contains(q, ignoreCase = true) || item.category.contains(q, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        val sorted = if (isAscending) {
            filtered.sortedBy { it.timestamp }
        } else {
            filtered.sortedByDescending { it.timestamp }
        }

        adapter.submitList(sorted)
        binding.txvEmpty.visibility = if (sorted.isEmpty()) View.VISIBLE else View.GONE
    }
}