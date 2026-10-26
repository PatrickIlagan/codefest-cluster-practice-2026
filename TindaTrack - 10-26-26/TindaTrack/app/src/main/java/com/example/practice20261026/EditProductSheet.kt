package com.example.practice20261026

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.practice20261026.data.Product
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityEditProductBtsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale

class EditProductSheet(
    private var productId: Long = 0L
) : BottomSheetDialogFragment(R.layout.activity_edit_product_bts) {
    private lateinit var binding: ActivityEditProductBtsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityEditProductBtsBinding.bind(view)
        val calendar = Calendar.getInstance()

        if (productId != -1L){
            val product = dao.getProductById(productId)
            if (product != null){
                binding.edtProductName.setText(product.name)
                binding.edtProductSTock.setText(product.quantity.toString())
                binding.edtProductPrice.setText(product.sellingPrice.toString())
                binding.edmProductCategory.setText(product.category)
                binding.edtProductUnit.setText(product.unit)
                binding.edtProductMinStock.setText(product.minThresh.toString())

                binding.btnSaveChangesProduct.setOnClickListener {
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

                    val newProduct = product.copy(
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

                    dao.updateProduct(newProduct)
                    Toast.makeText(requireContext(), "Edited Successfully!", Toast.LENGTH_SHORT).show()

                    dismiss()

                }

                binding.btnDeleteProduct.setOnClickListener {
                    dao.deleteProduct(product)
                    Toast.makeText(requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), ProductActivity::class.java))
                    dismiss()
                    requireActivity().finish()
                }




            }
        }

    }
    fun Long.toDate(): String = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(this)
}