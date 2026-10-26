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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.practice20261026.data.Product
import com.example.practice20261026.data.Transaction
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityProductBinding
import com.example.practice20261026.databinding.ActivityTransactionBinding
import com.example.practice20261026.databinding.ItemProductBinding
import com.example.practice20261026.databinding.ItemProductBinding.inflate
import com.example.practice20261026.databinding.ItemTransactionBinding
import com.example.practice20261026.utilities.SessionManager
import com.example.practice20261026.utilities.UniversalAdapter
import com.example.practice20261026.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import kotlin.text.contains

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionBinding
    private var rawList: List<Transaction> = emptyList()
    private lateinit var transactionAdapter: UniversalAdapter<Transaction, ItemTransactionBinding >
    private var isAscending: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        transactionAdapter = UniversalAdapter(
            inflate = ItemTransactionBinding::inflate,
            bind = { b, item ->
                b.txvTransactionName.text = item.customerName
                b.txvNetAmount.text = item.netAmount.toString()
                b.txvTotalAmount.text = item.totalAmount.toString()

                val product = dao.getProductByNameAndUserId(userId, item.productName)
                if (product != null){
                    b.txvTransactionStatus.text = product.status
                }


            },
            onItemClick = { item ->
                val intent = Intent(this, DetailsTransactionActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rcvTransaction.adapter = transactionAdapter
        binding.rcvTransaction.layoutManager = LinearLayoutManager(this)
        binding.rcvTransaction.isNestedScrollingEnabled = false

        binding.edtTransactionSearch.doAfterTextChanged { filter() }
        binding.cpgTransactionCategory.setOnCheckedStateChangeListener { _, _ -> filter()  }
        binding.btnDateFilter.setOnClickListener {
            isAscending = !isAscending
            filter()
        }
        lifecycleScope.launch {
            dao.getAllTransactions().collect { item ->
                rawList = item
                filter()
            }
        }

        setupBottomNav(binding.bottomNav, R.id.navTransactions)

        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
            finish()
        }
    }


    private fun filter() {
        var filtered = rawList
        val query = binding.edtTransactionSearch.text.toString().trim()
        val chipSelected = findViewById<Chip>(binding.cpgTransactionCategory.checkedChipId)?.text?.toString() ?: "All"

        if (query.isNotBlank()){
            filtered = filtered.filter {
                it.customerName.contains(query, ignoreCase = true)
            }
        }

        if (chipSelected != "All"){
            filtered = filtered.filter { it.type.contains(chipSelected, ignoreCase = true) }
        }

        filtered = if (isAscending) {
            filtered.sortedBy { it.timestamp }
        } else {
            filtered.sortedByDescending { it.timestamp }
        }

        transactionAdapter.submitList(filtered)
    }
}