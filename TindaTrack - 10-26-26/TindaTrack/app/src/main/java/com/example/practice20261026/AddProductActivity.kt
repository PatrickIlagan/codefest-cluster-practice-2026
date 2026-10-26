package com.example.practice20261026

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.intSetOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261026.data.Product
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityAddProductBinding
import com.example.practice20261026.databinding.ActivityLoginBinding
import com.example.practice20261026.utilities.SessionManager
import java.text.SimpleDateFormat
import java.util.Locale

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val category = arrayOf("Canned & Packaged", "Rice & Grains", " Beverages & Dairy", "Snacks & Personal Care")
        binding.edmProductCategory.setSimpleItems(category)

        val calendar = Calendar.getInstance()




        binding.btnAddProduct.setOnClickListener {
            val name = binding.edtProductName.text.toString().trim()
            val category = binding.edmProductCategory.text.toString().trim()
            val sellingPrice = binding.edtProductPrice.text.toString().trim()
            val stock = binding.edtProductSTock.text.toString().trim()
            val minThresh = binding.edtProductMinStock.text.toString().trim()
            val unit = binding.edtProductUnit.text.toString().trim()

            val timestamp = calendar.timeInMillis
            val date = timestamp.toDate()

            if (name.isBlank()){
                binding.edtProductName.error = "Enter Product Name"
                binding.edtProductName.requestFocus()
                return@setOnClickListener
            }
            if (category.isBlank()){
                binding.edmProductCategory.error = "Enter Product Category"
                binding.edmProductCategory.requestFocus()
                return@setOnClickListener
            }
            if (sellingPrice.isBlank()){
                binding.edtProductPrice.error = "Enter Selling Price"
                binding.edtProductPrice.requestFocus()
                return@setOnClickListener
            }
            if (stock.isBlank()){
                binding.edtProductSTock.error = "Enter Product quantity"
                binding.edtProductSTock.requestFocus()
                return@setOnClickListener
            }
            if (minThresh.isBlank()){
                binding.edtProductMinStock.error = "Enter Minimum Threshold"
                binding.edtProductMinStock.requestFocus()
                return@setOnClickListener
            }
            if (unit.isBlank()){
                binding.edtProductUnit.error = "Enter Product Unit"
                binding.edtProductUnit.requestFocus()
                return@setOnClickListener
            }



            val doublePrice = sellingPrice.toDoubleOrNull() ?: 0.0
            val intStock = stock.toIntOrNull() ?: 0
            val intThresh = minThresh.toIntOrNull() ?: 0

            if (intStock < 0){
                binding.edtProductSTock.error = "Insufficient Stock"
                binding.edtProductSTock.requestFocus()
                return@setOnClickListener
            }

            if (doublePrice < 0){
                binding.edtProductSTock.error = "Negative Price"
                binding.edtProductSTock.requestFocus()
                return@setOnClickListener
            }

            if (intThresh < 0){
                binding.edtProductSTock.error = "Negative Threshold"
                binding.edtProductSTock.requestFocus()
                return@setOnClickListener
            }

            val status = if (intStock < intThresh) {
                "Low Stock"
            } else if (intStock == 0){
                "Out-of-Stock"
            } else {
                "In Stock"
            }

            val newProduct = Product(
                userId = userId,
                name = name,
                category = category,
                sellingPrice = doublePrice,
                quantity = intStock,
                minThresh = intThresh,
                unit = unit,
                date = date,
                timestamp = timestamp,
                status = status,
            )

            val id = dao.insertProduct(newProduct)
            Toast.makeText(this, "Product $name Added!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, DetailsProductActivity::class.java).apply {
                putExtra("ID", id)
            }
            startActivity(intent)
            finish()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }

    }
    fun Long.toDate(): String = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(this)

}