package com.example.backend10282026

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.backend10282026.data.Food
import com.example.backend10282026.data.dao
import com.example.backend10282026.databinding.ActivityEditFoodBtsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditFoodSheet(
    private var foodId: Long = 0L
) : BottomSheetDialogFragment(R.layout.activity_edit_food_bts) {
    private lateinit var binding: ActivityEditFoodBtsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityEditFoodBtsBinding.bind(view)



        val food = dao.getFoodById(foodId)
        if (food != null){
            binding.edtFoodDesc.setText(food.description)
            binding.edtFoodName.setText(food.name)
            binding.edtFoodPrice.setText(String.format("%.2f", food.price))

            val category = arrayOf("Rice Meals", "Noodles & Pasta", "Snacks", "Drinks & Desserts")
            binding.edmFoodCategory.setSimpleItems(category)
            binding.edmFoodCategory.setText(food.category, false)


            binding.btnSaveChangesFood.setOnClickListener{
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

                val newFood = food.copy(
                    name = name,
                    category = category,
                    price = priceDouble,
                    description = description,
                )

                dao.updateFood(newFood)
                dismiss()
                Toast.makeText(requireContext(), "Successfully made new Food!", Toast.LENGTH_SHORT).show()
            }
        }
        }


}