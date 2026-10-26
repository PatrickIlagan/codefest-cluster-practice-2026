package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityDetailsProductBinding
import com.example.practice20261026.databinding.ActivityMainBinding
import com.example.practice20261026.utilities.SessionManager
import com.google.android.material.snackbar.Snackbar

class DetailsProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailsProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val productId = intent.getLongExtra("ID", -1L)

        if (productId != -1L){
            val product = dao.getProductById(productId)
            if (product != null){
                binding.txvProductName.text = product.name
                binding.txvProductStock.text = product.quantity.toString()
                binding.txvProductPrice.text = product.sellingPrice.toString()
                binding.txvProductCategory.text = product.category
                binding.txvProductUnit.text = product.unit
                binding.txvProductMinStock.text = product.minThresh.toString()

                binding.btnEditProduct.setOnClickListener {
                    EditProductSheet(productId).show(supportFragmentManager, "EditProductSheet")
                }

                binding.btnDeleteProduct.setOnClickListener {
                    dao.deleteProduct(product)
                    Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ProductActivity::class.java))
                    finish()
                }


                binding.btnBack.setOnClickListener {
                    startActivity(Intent(this, ProductActivity::class.java))
                    finish()
                }
            }
        }




    }
}