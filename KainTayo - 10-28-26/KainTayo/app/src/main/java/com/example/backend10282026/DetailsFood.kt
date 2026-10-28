package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.backend10282026.data.CartItem
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityDetailsFoodBinding
import com.example.backend10282026.databinding.ActivityMenuBinding
import com.example.frontend10282026.utilities.SessionManager

class DetailsFood : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsFoodBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsFoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val foodId = intent.getLongExtra("ID", -1L)

        if (foodId != -1L){
            val food = dao.getFoodById(foodId)
            val cartItem = dao.getCartItemByFoodId(foodId)
            if (food != null){
                var quantity = 1
                var amount = quantity * food.price


                binding.txvFoodName.text = food.name
                binding.txvFoodAmount.text = String.format("PHP %.2f", food.price)
                binding.txvFoodCategory.text = food.category
                binding.txvFoodQuantity.text = quantity.toString()
                binding.txvFoodDescription.text = food.description
                binding.imageView5.setImageResource(food.image)
                binding.txvAmount.text = String.format("PHP %.2f", amount)

                binding.btnQtyAdd.setOnClickListener {
                    quantity++
                    amount = quantity * food.price
                    binding.txvFoodQuantity.text = quantity.toString()
                    binding.txvAmount.text = String.format("PHP %.2f", amount)
                }
                binding.btnQtyMinus.setOnClickListener {
                    if (quantity > 1){
                        quantity--
                    }
                    amount = quantity * food.price
                    binding.txvFoodQuantity.text = quantity.toString()
                    binding.txvAmount.text = String.format("PHP %.2f", amount)

                }

                if (cartItem != null){
                    quantity = cartItem.quantity
                    amount = quantity * food.price
                    binding.txvFoodQuantity.text = quantity.toString()
                    binding.txvAmount.text = String.format("PHP %.2f", amount)
                    binding.btnAddToCart.setText("View Cart")
                    binding.btnAddToCart.setOnClickListener {
                        startActivity(Intent(this, CartCheckoutActivity::class.java))
                        finish()
                    }
                }

                binding.btnAddToCart.setOnClickListener {
                    val cartItemInsert = CartItem(
                        foodId = foodId,
                        userId = userId,
                        quantity = quantity,
                        price = food.price
                    )
                    dao.insertCartItem(cartItemInsert)
                    startActivity(Intent(this, MenuActivity::class.java))
                    finish()
                    Toast.makeText(this, "Sucessfully Added ${food.name}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
        }
    }
}