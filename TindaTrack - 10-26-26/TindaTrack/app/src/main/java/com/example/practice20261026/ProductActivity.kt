package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.practice20261026.data.Product
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityMainBinding
import com.example.practice20261026.databinding.ActivityProductBinding

import com.example.practice20261026.databinding.ItemProductBinding
import com.example.practice20261026.utilities.SessionManager
import com.example.practice20261026.utilities.UniversalAdapter
import com.example.practice20261026.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductBinding
    private var rawList: List<Product> = emptyList()
    private lateinit var productAdapter: UniversalAdapter<Product, ItemProductBinding >
    private var isAscending: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val search = intent.getStringExtra("SEARCH")

        binding.edtProductSearch.setText(search)

        productAdapter = UniversalAdapter(
            inflate = ItemProductBinding::inflate,
            bind = { b, item ->
                b.txvProductName.text = item.name
                b.txvProductPrice.text = item.sellingPrice.toString()
                b.txvProductCategory.text = item.category
                b.txvProductStock.text = item.quantity.toString()
                b.txvProductStockStatus.text = item.status
            },
            onItemClick = { item ->
                val intent = Intent(this, DetailsProductActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rcvProduct.adapter = productAdapter
        binding.rcvProduct.layoutManager = GridLayoutManager(this, 2)
        binding.rcvProduct.isNestedScrollingEnabled = false

        binding.edtProductSearch.doAfterTextChanged { filter() }
        binding.cpgProductCategory.setOnCheckedStateChangeListener { _, _ -> filter()  }
        binding.btnDateFilter.setOnClickListener {
            isAscending = !isAscending
            filter()
        }

        lifecycleScope.launch {
            dao.getAllProductsByUserId(userId).collect { itemList ->
                rawList = itemList
                filter()
            }
        }

        setupBottomNav(binding.bottomNav, R.id.navProducts)

        binding.fabAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
            finish()
        }
    }


    private fun filter() {
        var filtered = rawList
        val query = binding.edtProductSearch.text.toString().trim()
        val chipSelected = findViewById<Chip>(binding.cpgProductCategory.checkedChipId)?.text?.toString() ?: "All"

        if (query.isNotBlank()){
            filtered = filtered.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }

        if (chipSelected != "All"){
            filtered = filtered.filter { it.category.contains(chipSelected, ignoreCase = true) }
        }

        filtered = if (isAscending) {
            filtered.sortedBy { it.timestamp }
        } else {
            filtered.sortedByDescending { it.timestamp }
        }

        productAdapter.submitList(filtered)
    }
}