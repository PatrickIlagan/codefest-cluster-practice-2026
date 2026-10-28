package com.example.backend10282026

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.Order
import com.example.backend10282026.data.OrderItem
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityMenuBinding
import com.example.backend10282026.databinding.ActivityOrdersBinding
import com.example.backend10282026.databinding.ItemCardFoodMenuBinding
import com.example.backend10282026.databinding.ItemCardOrderBinding
import com.example.backend10282026.databinding.ItemListOrderBinding
import com.example.backend10282026.utilities.UniversalAdapter
import com.example.backend10282026.utilities.setupBottomNav
import com.example.frontend10282026.utilities.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrdersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var orderAdapter: UniversalAdapter<Order, ItemCardOrderBinding>
    private var isAscending: Boolean = false
    private var rawList: List<Order> = emptyList()
    private var totalAmount: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        orderAdapter = UniversalAdapter(
            inflate = ItemCardOrderBinding::inflate,
            bind = { b, item ->
                b.txvUsername.text = item.customerName
                b.txvOrderType.text = item.orderType
                b.txvDateAdded.text = item.date

                var orderItem = dao.getAllOrderItemListByOrderId(item.id)

                val totalAmount = orderItem.sumOf { it.price * it.quantity }
                b.txvTotalAmount.text = String.format("PHP %.2f", totalAmount)


                val orderItemAdapter = UniversalAdapter<OrderItem, ItemListOrderBinding>(
                    inflate = ItemListOrderBinding::inflate,
                    bind = { b2, item2 ->
                        val amount = item2.quantity * item2.price
                        b2.txvFoodName.text = item2.foodName
                        b2.txvFoodAmount.text = String.format("PHP %.2f", amount)
                        b2.txvFoodQuantity.text = item2.quantity.toString()

                    },
                    onItemClick = {item ->

                    }
                )

                lifecycleScope.launch {
                    dao.getAllOrderItemByOrderId(item.id).collect { list ->
                        orderItemAdapter.submitList(list)
                    }
                }

                b.rcvOrder.adapter = orderItemAdapter
                b.rcvOrder.layoutManager = LinearLayoutManager(this)
                b.rcvOrder.isNestedScrollingEnabled=false
            },
            onItemClick = {item ->

            }
        )

        binding.rcvOrderHistory.adapter = orderAdapter
        binding.rcvOrderHistory.layoutManager = LinearLayoutManager(this)
        binding.rcvOrderHistory.isNestedScrollingEnabled=false

        binding.edtSearchOrder.doAfterTextChanged { filter() }
        binding.btnOrderSateSort.setOnClickListener {
            isAscending = !isAscending
            filter()
        }

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartCheckoutActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllOrderByUserId(userId).collect { list ->
                rawList = list
                filter()
            }
        }
        setupBottomNav(binding.bottomNav, com.example.backend10282026.R.id.menuOrders)

    }
    private fun filter(){
        var filtered = rawList
        val query = binding.edtSearchOrder.text.toString().trim()

        if (query.isNotBlank()){
            filtered = filtered.filter{it.date.contains(query, ignoreCase = true)}
        }

        filtered = if (isAscending){
             filtered.sortedBy { it.timestamp }
        } else {
            filtered.sortedByDescending { it.timestamp }
        }

        orderAdapter.submitList(filtered)
    }
}