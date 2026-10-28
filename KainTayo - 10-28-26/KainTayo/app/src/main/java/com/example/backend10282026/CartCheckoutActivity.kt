package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backend10282026.data.CartItem
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.Order
import com.example.backend10282026.data.OrderItem
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityCartCheckoutBinding
import com.example.backend10282026.databinding.ActivityDetailsFoodBinding
import com.example.backend10282026.databinding.ActivityMenuBinding
import com.example.backend10282026.databinding.ActivityMenuBinding.inflate
import com.example.backend10282026.databinding.ItemCardCartFoodBinding
import com.example.backend10282026.utilities.UniversalAdapter
import com.example.frontend10282026.utilities.SessionManager
import com.example.frontend10282026.utilities.sendNotif
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class CartCheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartCheckoutBinding
    private lateinit var cartAdapter: UniversalAdapter<CartItem, ItemCardCartFoodBinding>
    private  var totalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCartCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val user = dao.getUserById(userId)
        if (user != null){
            binding.btnCheckout.setOnClickListener {
                val orderType = findViewById<RadioButton>(binding.rdgOrderType.checkedRadioButtonId)?.text?.toString() ?: "Null"
                val notes = binding.edtCustomerNotes.toString().trim()
                val cartItem = dao.getAllCardItemListByUserId(userId)

                if(notes.isBlank()){
                    binding.edtCustomerNotes.error = "Missing notes!"
                    binding.edtCustomerNotes.requestFocus()
                    return@setOnClickListener
                }

                if(cartItem.isEmpty()){
                    Toast.makeText(this, "No Cart Items.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val newOrder = Order(
                    userId = userId,
                    customerName = user.name,
                    notes = notes,
                    total = totalAmount,
                    orderType = orderType,
                    timestamp = System.currentTimeMillis(),
                    date = System.currentTimeMillis().toDate()
                )
                val orderId = dao.insertOrder(newOrder)

                cartItem.forEach { carty ->
                    val food = dao.getFoodById(carty.id)
                    if(food != null){
                        val orderItem = OrderItem(
                            orderId = orderId,
                            foodName = food.name,
                            category = food.category,
                            quantity = carty.quantity,
                            image = food.image,
                            price = carty.price
                        )

                        dao.insertOrderItem(orderItem)
                    }
                }

                dao.clearCart(userId)
                sendNotif("New Order: $orderId!","You have successfully bought a new order" )
                Toast.makeText(this, "Order $orderId Added!", Toast.LENGTH_SHORT).show()


            }
        }


        cartAdapter = UniversalAdapter(
            inflate = ItemCardCartFoodBinding::inflate,
            bind = { b, item ->
                val food = dao.getFoodById(item.foodId)
                if (food != null) {
                    var quantity = item.quantity
                    var amount = quantity * food.price

                    b.txvFoodQuantity.text = item.quantity.toString()
                    b.txvAmount.text = String.format("PHP %.2f", amount)
                    b.txvFoodAmount.text = String.format("PHP %.2f", food.price)
                    b.txvFoodCategory.text = food.category
                    b.txvFoodName.text = food.name
                    b.imageView3.setImageResource(food.image)

                    b.btnQtyAdd.setOnClickListener {
                        quantity++
                        amount = quantity * food.price
                        b.txvFoodQuantity.text = quantity.toString()
                        b.txvAmount.text = String.format("PHP %.2f", amount)
                        val updatedQuantity = item.copy(
                            quantity = quantity
                        )
                        dao.updateCartItem(updatedQuantity)
                    }
                    b.btnQtyMinus.setOnClickListener {
                        if (quantity > 1) {
                            quantity--
                        } else {
                            dao.deleteCartItem(item.id)
                        }
                        amount = quantity * food.price
                        b.txvFoodQuantity.text = quantity.toString()
                        b.txvAmount.text = String.format("PHP %.2f", amount)
                        val updatedQuantity = item.copy(
                            quantity = quantity
                        )
                        dao.updateCartItem(updatedQuantity)
                    }
                }

            },
            onItemClick = {

            }
        )

        binding.rcvCart.adapter = cartAdapter
        binding.rcvCart.layoutManager = LinearLayoutManager(this)
        binding.rcvCart.isNestedScrollingEnabled=false

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllCardItemByUserId(userId).collect { cart ->
                cart.forEach { item ->


                }
                totalAmount = cart.sumOf { item ->
                    item.price * item.quantity
                }

                binding.txvTotalAmount.text = String.format("PHP %.2f", totalAmount)
                binding.txvTotalAmount2.text = String.format("PHP %.2f", totalAmount)
            }
        }

        lifecycleScope.launch {
            dao.getAllCardItemByUserId(userId).collect { cart ->
                cartAdapter.submitList(cart)
            }
        }
    }
    fun Long.toDate(): String = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(this)
}