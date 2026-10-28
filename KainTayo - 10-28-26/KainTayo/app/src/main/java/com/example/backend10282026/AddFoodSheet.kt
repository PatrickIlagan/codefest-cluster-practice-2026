package com.example.backend10282026

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityAddFoodBtsBinding
import com.example.backend10282026.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddFoodSheet() : BottomSheetDialogFragment(R.layout.activity_add_food_bts) {
    private lateinit var binding: ActivityAddFoodBtsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityAddFoodBtsBinding.bind(view)

        val category = arrayOf("Rice Meals", "Noodles & Pasta", "Snacks", "Drinks & Desserts")
        binding.edmFoodCategory.setSimpleItems(category)
        binding.edmFoodCategory.setText("Rice Meals", false)

        binding.AddFood.setOnClickListener{
            val name = binding.edtFoodName.text.toString().trim()
            val category = binding.edmFoodCategory.text.toString().trim()
            val price = binding.edtFoodPrice.text.toString().trim()
            val description = binding.edtFoodDesc.text.toString().trim()

            if (name.isBlank()){
                binding.edtFoodName.error = "Missing Name"
                binding.edtFoodName.requestFocus()
                return@setOnClickListener
            }

            if (category.isBlank()){
                binding.edmFoodCategory.error = "Missing Category"
                binding.edmFoodCategory.requestFocus()
                return@setOnClickListener
            }

            if (price.isBlank()){
                binding.edtFoodPrice.error = "Missing Price"
                binding.edtFoodPrice.requestFocus()
                return@setOnClickListener
            }

            if (description.isBlank()){
                binding.edtFoodDesc.error = "Missing Description"
                binding.edtFoodDesc.requestFocus()
                return@setOnClickListener
            }

            val priceDouble = price.toDoubleOrNull() ?: 0.0

            val newFood = Food(
                name = name,
                category = category,
                price = priceDouble,
                description = description,
                image = 0,
            )

            dao.insertFood(newFood)
            dismiss()
            Toast.makeText(requireContext(), "Successfully made new Food!", Toast.LENGTH_SHORT).show()
        }
    }
}