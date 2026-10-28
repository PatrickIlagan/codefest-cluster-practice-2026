package com.example.backend10282026

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.Order
import com.example.backend10282026.data.OrderItem
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityMainBinding

import com.example.backend10282026.databinding.ItemCardFoodMenuBinding
import com.example.backend10282026.databinding.ItemCardFoodMenuBinding.inflate
import com.example.backend10282026.databinding.ItemCardOrderBinding
import com.example.backend10282026.databinding.ItemCardOrderBinding.inflate
import com.example.backend10282026.databinding.ItemListOrderBinding
import com.example.backend10282026.databinding.ItemListOrderBinding.inflate
import com.example.backend10282026.databinding.ItemPagerBinding
import com.example.backend10282026.utilities.UniversalAdapter
import com.example.backend10282026.utilities.setupBottomNav
import com.example.frontend10282026.utilities.SessionManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.get
import kotlin.collections.set

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bannerAdapter: UniversalAdapter<Int, ItemPagerBinding>
    private lateinit var orderAdapter: UniversalAdapter<Order, ItemCardOrderBinding>

    private var rawList: List<Food> = emptyList()
    var isSwiping = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        if (!session.isLogged()){
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val user = dao.getUserById(userId)
        if (user != null){
            binding.txvUsername.text = user.name

        }


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

        binding.rcvOrderHistoryShort.adapter = orderAdapter
        binding.rcvOrderHistoryShort.layoutManager = LinearLayoutManager(this)
        binding.rcvOrderHistoryShort.isNestedScrollingEnabled=false

        binding.catRice.setOnClickListener{
            startActivity(Intent(this, MenuActivity::class.java).putExtra("SELECTED", "Rice Meals"))
            finish()
        }

        binding.catNoodles.setOnClickListener{
            startActivity(
                Intent(this, MenuActivity::class.java).putExtra(
                    "SELECTED",
                    "Noodles & Pasta"
                )
            )
            finish()
        }

        binding.catSnacks.setOnClickListener{
            startActivity(Intent(this, MenuActivity::class.java).putExtra("SELECTED", "Snacks"))
            finish()
        }
        binding.catDrinks.setOnClickListener{
            startActivity(
                Intent(this, MenuActivity::class.java).putExtra(
                    "SELECTED",
                    "Drinks & Desserts"
                )
            )
            finish()
        }




        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartCheckoutActivity::class.java))
            finish()
        }


        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        binding.btnOrders.setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
            finish()
        }




        setupBottomNav(binding.bottomNav, R.id.menuHome)


        bannerAdapter = UniversalAdapter(
            inflate = ItemPagerBinding::inflate,
            bind = { b, cardType ->
                b.card1.isVisible = (cardType == 0)
                b.card2.isVisible = (cardType == 1)
                b.card3.isVisible = (cardType == 2)
                b.card4.isVisible = (cardType == 3)

                when (cardType) {
                    0 ->
                        b.card1.setOnClickListener{
                            startActivity(Intent(this, MenuActivity::class.java).putExtra("SELECTED", "Rice Meals"))
                            finish()
                        }
                    1 ->
                        b.card2.setOnClickListener {
                            startActivity(
                                Intent(this, MenuActivity::class.java).putExtra(
                                    "SELECTED",
                                    "Noodles & Pasta"
                                )
                            )
                            finish()
                        }

                    3 ->
                        b.card3.setOnClickListener {
                            startActivity(Intent(this, MenuActivity::class.java).putExtra("SELECTED", "Snacks"))
                            finish()
                        }

                    2 ->
                        b.card4.setOnClickListener {
                            startActivity(
                                Intent(this, MenuActivity::class.java).putExtra(
                                    "SELECTED",
                                    "Drinks & Desserts"
                                )
                            )
                            finish()
                        }
                }
            }
        )

        binding.pagerBento.adapter = bannerAdapter

        TabLayoutMediator(binding.tabDots, binding.pagerBento){ tabs, _ ->
            tabs.text = "\u2b24"
        }.attach()

        binding.pagerBento.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                isSwiping = (state == ViewPager2.SCROLL_STATE_DRAGGING)
            }
        })

        bannerAdapter.submitList(listOf(0,1,2,3))

        lifecycleScope.launch {
            while(isActive){
                delay(3000)
                if (!isSwiping && bannerAdapter.itemCount > 0){
                    val nextItem = (binding.pagerBento.currentItem + 1) % bannerAdapter.itemCount
                    binding.pagerBento.setCurrentItem(nextItem, true)
                }
            }
        }

        lifecycleScope.launch {
            dao.getAllOrderByUserId(userId).collect { list ->
                val filtered = list.sortedByDescending { it.timestamp }.take(3)
                orderAdapter.submitList(filtered)
            }
        }




        lifecycleScope.launch {
            dao.getAllOrderByUserId(userId).collect { list ->
                val totalCount = list.size
                binding.txvOrderAmount.text = totalCount.toString()

               val allOrderItem = list.flatMap { order ->
                   dao.getAllOrderItemListByOrderId(order.id)
               }

                val totalSpent = allOrderItem.sumOf { it.quantity * it.price }

                binding.txvSpentAmount.text = String.format("PHP %.2f", totalSpent)

                val totalOrderItem = allOrderItem.sumOf { it.quantity }

                val topCategoryEntry = allOrderItem
                    .groupBy { it.category }
                    .maxByOrNull { (_,items) -> items.sumOf { it.quantity } }

                val mostBoughtCategory = topCategoryEntry?.key?: "N/A"
                val totalBoughtCategory = topCategoryEntry?.value?.sumOf { it.quantity } ?: 0

                val percentage = if (totalOrderItem > 0) (totalBoughtCategory * 100) / totalOrderItem else 0

                binding.txvFoodCategory.text = mostBoughtCategory
                binding.txvCategoryPercentage.text = "$percentage% of your orders come from here"
            }
        }


    }


}