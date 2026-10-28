package com.example.backend10282026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityMainBinding
import com.example.backend10282026.databinding.ActivityMenuBinding
import com.example.backend10282026.databinding.ItemCardFoodMenuBinding
import com.example.backend10282026.utilities.UniversalAdapter
import com.example.backend10282026.utilities.setupBottomNav
import com.example.frontend10282026.utilities.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.text.contains

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var foodAdapter: UniversalAdapter<Food, ItemCardFoodMenuBinding>
    private var rawList: List<Food> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val isAdmin = session.getUserRole() == "ADMIN"

        val chipSelected = intent.getStringExtra("SELECTED")?: ""

        if (chipSelected.isNotBlank()){
            if (chipSelected == "Rice Meals"){
                binding.chip8.isChecked = true
            } else if (chipSelected == "Noodles & Pasta") {
                binding.chip7.isChecked= true
            } else if (chipSelected == "Snacks") {
                binding.chip6.isChecked= true
            }else if (chipSelected == "Drinks & Desserts") {
                binding.chip.isChecked= true
            }
        }

        foodAdapter = UniversalAdapter(
            inflate = ItemCardFoodMenuBinding::inflate,
            bind = { b, item ->
                b.txvFoodName.text = item.name
                b.txvFoodCategory.text = item.category
                b.txvFoodAmount.text = String.format("PHP %.2f", item.price)
                b.imageView6.setImageResource(item.image)

                b.btnAdminEdit.isVisible = isAdmin
                b.btnAdminDelete.isVisible = isAdmin

                b.btnAdminEdit.setOnClickListener {
                    EditFoodSheet(item.id).show(supportFragmentManager, "EditFoodSheet")
                }
                b.btnAdminDelete.setOnClickListener {
                    dao.deleteFood(item)
                    Snackbar.make(binding.root, "DELETED ${item.name}", Snackbar.LENGTH_LONG)
                        .setAnchorView(binding.floatingActionButton)
                        .setAction("UNDO"){
                            dao.insertFood(item)
                        }
                        .show()
                }
            },
            onItemClick = {item ->
                val intent = Intent(this, DetailsFood::class.java).apply {
                   putExtra("ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartCheckoutActivity::class.java))
            finish()
        }

        binding.rcvFoodMenu.adapter = foodAdapter
        binding.rcvFoodMenu.layoutManager = GridLayoutManager(this,2)
        binding.rcvFoodMenu.isNestedScrollingEnabled=false

        binding.edtSearchFood.doAfterTextChanged { filter() }
        binding.cpgFoodCategory.setOnCheckedChangeListener { _, _ -> filter()  }


        binding.floatingActionButton.isVisible = isAdmin
        binding.floatingActionButton.setOnClickListener {
            AddFoodSheet().show(supportFragmentManager, "AddFoodSheet")
        }


        setupBottomNav(binding.bottomNav, R.id.menuSearch)
        lifecycleScope.launch {
            dao.getAllFood().collect { list ->
                rawList = list
                filter()
            }
        }

    }
    private fun filter(){
        var filtered = rawList
        val query = binding.edtSearchFood.text.toString().trim()
        val selectedChip = findViewById<Chip>(binding.cpgFoodCategory.checkedChipId)?.text?.toString()

        if (query.isNotBlank()){
            filtered = filtered.filter{it.name.contains(query, ignoreCase = true)}
        }

        if (selectedChip != "All" && selectedChip != null){
            filtered = filtered.filter { it.category.contains(selectedChip, ignoreCase = true) }
        }

        foodAdapter.submitList(filtered)
    }
}