package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backend10282026.data.Order
import com.example.backend10282026.data.OrderItem
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityMainBinding
import com.example.backend10282026.databinding.ActivityProfileBinding
import com.example.backend10282026.databinding.ItemCardOrderBinding
import com.example.backend10282026.databinding.ItemCardOrderBinding.inflate
import com.example.backend10282026.databinding.ItemListOrderBinding
import com.example.backend10282026.databinding.ItemListOrderBinding.inflate
import com.example.backend10282026.utilities.UniversalAdapter
import com.example.backend10282026.utilities.setupBottomNav
import com.example.frontend10282026.utilities.SessionManager
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var orderAdapter: UniversalAdapter<Order, ItemCardOrderBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        if (userId != -1L){
            val user = dao.getUserById(userId)
            if (user != null){
                binding.txvUsername.text = user.name
                binding.txvUserRole.text = user.role
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

                binding.btnCart2.setOnClickListener {
                    startActivity(Intent(this, CartCheckoutActivity::class.java))
                    finish()
                }

                binding.crdLogOut.setOnClickListener {
                    session.logout()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }


                binding.rcvOrderHistoryShort.adapter = orderAdapter
                binding.rcvOrderHistoryShort.layoutManager = LinearLayoutManager(this)
                binding.rcvOrderHistoryShort.isNestedScrollingEnabled=false
                setupBottomNav(binding.bottomNav, R.id.menuProfile)
                lifecycleScope.launch {
                    dao.getAllOrderByUserId(userId).collect { list ->
                        val filtered = list.sortedByDescending { it.timestamp }.take(3)
                        orderAdapter.submitList(filtered)
                    }
                }
            }
        }
    }
}