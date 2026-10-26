package com.example.practice20261026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat.enableEdgeToEdge
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.practice20261026.data.Product
import com.example.practice20261026.data.Transaction
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityMainBinding

import com.example.practice20261026.databinding.ItemPagerProductCategoryBinding
import com.example.practice20261026.databinding.ItemProductHorizontalBinding
import com.example.practice20261026.databinding.ItemTransactionBinding
import com.example.practice20261026.databinding.ItemTransactionBinding.inflate
import com.example.practice20261026.utilities.SessionManager
import com.example.practice20261026.utilities.UniversalAdapter
import com.example.practice20261026.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: UniversalAdapter<Product, ItemProductHorizontalBinding>
    private lateinit var transactionAdapter: UniversalAdapter<Transaction, ItemTransactionBinding>
    private lateinit var bannerAdapter: UniversalAdapter<Pair<Int, List<Product>>, ItemPagerProductCategoryBinding>
    private var isUserSwiping = false
    private var rawList: List<Product> = emptyList()
    private var isAscending: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        productAdapter = UniversalAdapter(
            inflate = ItemProductHorizontalBinding::inflate,
            bind = { b, item ->
                b.txvProductName.text = item.name
                b.txvProductPrice.text = item.sellingPrice.toString()
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

        binding.rcvLatestProducts.adapter = productAdapter
        binding.rcvLatestProducts.layoutManager = LinearLayoutManager(this)
        binding.rcvLatestProducts.isNestedScrollingEnabled = false

        transactionAdapter = UniversalAdapter(
            inflate = ItemTransactionBinding::inflate,
            bind = { b, item ->


                b.txvTransactionName.text = item.customerName
                b.txvNetAmount.text = item.netAmount.toString()
                b.txvTotalAmount.text = item.totalAmount.toString()

                val product = dao.getProductByNameAndUserId(userId, item.productName)
                if (product != null) {
                    b.txvTransactionStatus.text = product.status

                    if (item.quantity == 0) {
                        b.root.strokeWidth = 6
                        b.root.strokeColor = "#ffffff".toColorInt()

                        dao.updateProduct(product = product.copy(status = "Out-of-Stock"))
                    } else if (item.quantity > 0 && product.minThresh > item.quantity) {
                        b.root.strokeWidth = 6
                        b.root.strokeColor = "#ffffff".toColorInt()

                        dao.updateProduct(product = product.copy(status = "Low Stock"))
                    } else {
                        dao.updateProduct(product = product.copy(status = "In Stock"))
                    }
                }
            },
            onItemClick = { item ->
                val intent = Intent(this, DetailsProductActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            }
        )

        binding.rcvLatestTransactions.adapter = transactionAdapter
        binding.rcvLatestTransactions.layoutManager = LinearLayoutManager(this)
        binding.rcvLatestTransactions.isNestedScrollingEnabled = false

        binding.btnSearch.setOnClickListener {
            val query = binding.edtProductSearch.text.toString().trim()
            val intent = Intent(this, ProductActivity::class.java).apply {
                putExtra("SEARCH", query)
            }
            startActivity(intent)
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }

        binding.cdvProduct.setOnClickListener {
            startActivity(Intent(this, ProductActivity::class.java))
            finish()
        }
        binding.cdvTransaction.setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllTransactions().collect { item ->
                val filtered = item.sortedByDescending { it.timestamp }.take(3)
                transactionAdapter.submitList(filtered)
            }
        }

        lifecycleScope.launch {
            dao.getAllProductsByUserId(userId).collect { item ->
                val filtered = item.sortedByDescending { it.timestamp }.take(3)
                productAdapter.submitList(filtered)
            }
        }

        lifecycleScope.launch {
            dao.getAllTransactions().collect { item ->
                val totalCashAmount = item.sumOf { it.netAmount }
                val totalUncollectedAmount =
                    item.filter { it.type == "Utang" }.sumOf { it.netAmount }
                binding.txvTotalCash.text = totalCashAmount.toString()
                binding.txvUtang.text = totalUncollectedAmount.toString()

            }


        }

        setupBottomNav(binding.bottomNav, R.id.navHome)

        bannerAdapter = UniversalAdapter(
            inflate = ItemPagerProductCategoryBinding::inflate,
            bind = { b, (cardType, product) ->
                b.layout1.isVisible = (cardType == 0)
                b.layout2.isVisible = (cardType == 1)
                b.layout3.isVisible = (cardType == 2)
                b.layout4.isVisible = (cardType == 3)

                val totalProducts = product.size
                val inStock = product.count { it.quantity > it.minThresh }   // 👈 from real stock, not the saved status text
                val percentage = if (totalProducts > 0) (inStock * 100) / totalProducts else 0

                when (cardType) {
                    0 -> {
                        b.cpi1.max = totalProducts
                        b.cpi1.progress = inStock
                        b.txvCategoryProductPercent1.text = "$percentage%"
                        b.txvProductCategory1.text = "Canned & Packaged"
                    }
                    1 -> {
                        b.cpi2.max = totalProducts
                        b.cpi2.progress = inStock
                        b.txvCategoryProductPercent2.text = "$percentage%"
                        b.txvProductCategory2.text = "Rice & Grains"
                    }
                    2 -> {
                        b.cpi3.max = totalProducts
                        b.cpi3.progress = inStock
                        b.txvCategoryProductPercent3.text = "$percentage%"
                        b.txvProductCategory3.text = "Beverages & Dairy"
                    }
                    3 -> {
                        b.cpi4.max = totalProducts
                        b.cpi4.progress = inStock
                        b.txvCategoryProductPercent4.text = "$percentage%"
                        b.txvProductCategory4.text = "Snacks & Personal Care"
                    }
                }
            }
        )   // 👈 bind ENDS here — everything below is outside it

        binding.pagerBento.adapter = bannerAdapter

        TabLayoutMediator(binding.tabDots, binding.pagerBento) { tab, _ ->
            tab.text = "\u2b24"
        }.attach()

        binding.pagerBento.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                isUserSwiping = (state == ViewPager2.SCROLL_STATE_DRAGGING)
            }
        })

        lifecycleScope.launch {
            while (isActive) {
                delay(3500)
                if (!isUserSwiping && bannerAdapter.itemCount > 0) {
                    val nextItem = (binding.pagerBento.currentItem + 1) % bannerAdapter.itemCount
                    binding.pagerBento.setCurrentItem(nextItem, true)
                }
            }
        }

        lifecycleScope.launch {
            dao.getAllProductsByUserId(userId).collect { products ->
                val canned = products.filter { it.category.equals("Canned & Packaged", ignoreCase = true) }
                val rice = products.filter { it.category.equals("Rice & Grains", ignoreCase = true) }
                val beverage = products.filter { it.category.equals("Beverages & Dairy", ignoreCase = true) }
                val snacks = products.filter { it.category.equals("Snacks & Personal Care", ignoreCase = true) }

                bannerAdapter.submitList(listOf(0 to canned, 1 to rice, 2 to beverage, 3 to snacks))   // 👈 0–3
            }
        }


    }
}



