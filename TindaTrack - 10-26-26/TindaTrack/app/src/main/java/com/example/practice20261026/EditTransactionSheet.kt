package com.example.practice20261026

import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.example.practice20261026.data.Transaction
import com.example.practice20261026.data.dao
import com.example.practice20261026.databinding.ActivityEditProductBtsBinding
import com.example.practice20261026.databinding.ActivityEditTransactionBtsBinding
import com.example.practice20261026.utilities.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

class EditTransactionSheet(
    private var transactionId: Long = 0L,

) : BottomSheetDialogFragment(R.layout.activity_edit_transaction_bts) {
    private lateinit var binding: ActivityEditTransactionBtsBinding
    private var totalAmount: Double = 0.0
    private var netAmount: Double = 0.0
    private var totalChange: Double = 0.0

    private var isGood: Boolean =false
    private var type: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = ActivityEditTransactionBtsBinding.bind(view)

        val session = SessionManager(requireContext())
        val userId = session.getUserID()

        var timestamp: Long = 0L



        if (transactionId != 0L) {
            val transaction = dao.getTransactionById(transactionId)
            val product = dao.getAllProductNameListByUserId(userId)
            if (transaction != null) {
                binding.edmTransactionProduct.setSimpleItems(product.toTypedArray())

                val calendar = Calendar.getInstance()

                binding.edtTransactionDueDate.setOnClickListener {
                    DatePickerDialog(
                        requireContext(),
                        { _, year, month, day ->
                            calendar.set(Calendar.YEAR, year)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, day)

                            timestamp = calendar.timeInMillis
                            binding.edtTransactionDueDate.setText("${month + 1}/${day}/${year}")
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }



                binding.btnSaveChangesTransaction.setOnClickListener {
                    val name = binding.edtTransactionName.text.toString().trim()
                    val productName = binding.edmTransactionProduct.text.toString().trim()
                    val cash = binding.edtTransactionCash.text.toString().trim()
                    val stock = binding.edtTransactionQuantity.text.toString().trim()

                    val dueDate = binding.edtTransactionDueDate.text.toString().trim()
                    val currentTimestamp = calendar.timeInMillis


                    if (name.isBlank()) {
                        binding.edtTransactionName.error = "Enter Customer Name"
                        binding.edtTransactionName.requestFocus()
                        return@setOnClickListener
                    }
                    if (cash.isBlank()) {
                        binding.edtTransactionCash.error = "Enter Cash Payment"
                        binding.edtTransactionCash.requestFocus()
                        return@setOnClickListener
                    }
                    if (stock.isBlank()) {
                        binding.edtTransactionQuantity.error = "Enter Cash Payment"
                        binding.edtTransactionQuantity.requestFocus()
                        return@setOnClickListener
                    }

                    if (productName.isBlank()) {
                        binding.edmTransactionProduct.error = "Enter Product"
                        binding.edmTransactionProduct.requestFocus()
                        return@setOnClickListener
                    }

                    val product = dao.getProductByNameAndUserId(userId, productName)


                    val doubleCash = cash.toDoubleOrNull() ?: 0.0
                    val intStock = stock.toIntOrNull() ?: 0

                    if (product != null) {
                        val transaction = transaction.copy(
                            productId = product.id,
                            productName = productName,
                            type = type,
                            quantity = intStock,
                            totalAmount = totalAmount,
                            netAmount = netAmount,
                            change = totalChange,
                            payment = doubleCash,
                            dueDate = dueDate,
                            dueDateTimestamp = timestamp,
                            currentDate = currentTimestamp.toDate(),
                            timestamp = currentTimestamp,
                            customerName = name
                        )

                        dao.updateTransaction(transaction)
                        Toast.makeText(
                            requireContext(),
                            "Succesfully Added Transaction ${transaction.id}",
                            Toast.LENGTH_SHORT
                        ).show()

                        dismiss()
                    }


                }

                binding.edmTransactionProduct.doAfterTextChanged {
                    val productName = binding.edmTransactionProduct.text.toString().trim()
                    val cash = binding.edtTransactionCash.text.toString().trim()
                    val stock = binding.edtTransactionQuantity.text.toString().trim()

                    if (productName.isNotBlank()) {
                        binding.edtTransactionCash.isEnabled = true
                        binding.edtTransactionQuantity.isEnabled = true
                    } else {
                        binding.edtTransactionCash.isEnabled = false
                        binding.edtTransactionQuantity.isEnabled = false
                        binding.edtTransactionCash.setText("")
                        binding.edtTransactionQuantity.setText("")
                    }

                    if (cash.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Cash Payment First to see Calculations"
                    }
                    if (stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Quantity First to see Calculations"
                    }

                    if (cash.isBlank() && stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Stock and Payment to See Calculations"
                    }


                    val doubleCash = cash.toDoubleOrNull() ?: 0.0
                    val intStock = stock.toIntOrNull() ?: 0


                    val product = dao.getProductByNameAndUserId(userId, productName)
                    if (product != null) {
                        val productCategory = product.category
                        val productPrice = product.sellingPrice
                        val quantity = product.quantity

                        binding.txvProductName.text = productName
                        binding.txvProductCategory.text = productCategory
                        binding.txvProductPrice.text = productPrice.toString()

                        if (quantity > intStock) {
                            totalAmount = productPrice * intStock
                            netAmount = totalAmount - doubleCash


                            binding.txvTotalAmount.text = totalAmount.toString()

                            if (netAmount < 0) {
                                binding.txvTransactionType.text = "Paid w/ Change"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else if (netAmount == 0.0) {
                                binding.txvTransactionType.text = "Paid"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else {
                                binding.txvTransactionType.text = "Debt"
                                binding.txvNetAmount.text = netAmount.toString()
                                binding.txvTransactionChange.text = "0"
                                totalChange = 0.0
                                isGood = true
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "$productName only has $quantity in stock",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
                binding.edtTransactionQuantity.doAfterTextChanged {
                    val productName = binding.edmTransactionProduct.text.toString().trim()
                    val cash = binding.edtTransactionCash.text.toString().trim()
                    val stock = binding.edtTransactionQuantity.text.toString().trim()

                    if (productName.isNotBlank()) {
                        binding.edtTransactionCash.isEnabled = true
                        binding.edtTransactionQuantity.isEnabled = true
                    } else {
                        binding.edtTransactionCash.isEnabled = false
                        binding.edtTransactionQuantity.isEnabled = false
                        binding.edtTransactionCash.setText("")
                        binding.edtTransactionQuantity.setText("")
                    }

                    if (cash.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Cash Payment First to see Calculations"
                    }
                    if (stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Quantity First to see Calculations"
                    }

                    if (cash.isBlank() && stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Stock and Payment to See Calculations"
                    }


                    val doubleCash = cash.toDoubleOrNull() ?: 0.0
                    val intStock = stock.toIntOrNull() ?: 0


                    val product = dao.getProductByNameAndUserId(userId, productName)
                    if (product != null) {
                        val productCategory = product.category
                        val productPrice = product.sellingPrice
                        val quantity = product.quantity

                        binding.txvProductName.text = productName
                        binding.txvProductCategory.text = productCategory
                        binding.txvProductPrice.text = productPrice.toString()

                        if (quantity > intStock) {
                            totalAmount = productPrice * intStock
                            netAmount = totalAmount - doubleCash


                            binding.txvTotalAmount.text = totalAmount.toString()

                            if (netAmount < 0) {
                                binding.txvTransactionType.text = "Paid w/ Change"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else if (netAmount == 0.0) {
                                binding.txvTransactionType.text = "Paid"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else {
                                binding.txvTransactionType.text = "Debt"
                                binding.txvNetAmount.text = netAmount.toString()
                                binding.txvTransactionChange.text = "0"
                                totalChange = 0.0
                                isGood = true
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "$productName only has $quantity in stock",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                binding.edtTransactionCash.doAfterTextChanged {
                    val productName = binding.edmTransactionProduct.text.toString().trim()
                    val cash = binding.edtTransactionCash.text.toString().trim()
                    val stock = binding.edtTransactionQuantity.text.toString().trim()

                    if (productName.isNotBlank()) {
                        binding.edtTransactionCash.isEnabled = true
                        binding.edtTransactionQuantity.isEnabled = true
                    } else {
                        binding.edtTransactionCash.isEnabled = false
                        binding.edtTransactionQuantity.isEnabled = false
                        binding.edtTransactionCash.setText("")
                        binding.edtTransactionQuantity.setText("")
                    }

                    if (cash.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Cash Payment First to see Calculations"
                    }
                    if (stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Quantity First to see Calculations"
                    }

                    if (productName.isBlank()) {
                        binding.txvTransactionError.text = "Enter Product Name"
                    }

                    if (cash.isBlank() && stock.isBlank()) {
                        binding.txvTransactionError.text =
                            "Enter Stock and Payment to See Calculations"
                    }


                    val doubleCash = cash.toDoubleOrNull() ?: 0.0
                    val intStock = stock.toIntOrNull() ?: 0


                    val product = dao.getProductByNameAndUserId(userId, productName)
                    if (product != null) {
                        val productCategory = product.category
                        val productPrice = product.sellingPrice
                        val quantity = product.quantity

                        binding.txvProductName.text = productName
                        binding.txvProductCategory.text = productCategory
                        binding.txvProductPrice.text = productPrice.toString()

                        if (quantity > intStock) {
                            totalAmount = productPrice * intStock
                            netAmount = totalAmount - doubleCash


                            binding.txvTotalAmount.text = totalAmount.toString()

                            if (netAmount < 0) {
                                binding.txvTransactionType.text = "Paid w/ Change"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else if (netAmount == 0.0) {
                                binding.txvTransactionType.text = "Paid"
                                binding.txvNetAmount.text = "0"
                                binding.txvTransactionChange.text = abs(netAmount).toString()
                                totalChange = abs(netAmount)
                                isGood = true
                            } else {
                                binding.txvTransactionType.text = "Debt"
                                binding.txvNetAmount.text = netAmount.toString()
                                binding.txvTransactionChange.text = "0"
                                totalChange = 0.0
                                isGood = true
                            }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "$productName only has $quantity in stock",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                binding.btnDeleteTransaction.setOnClickListener {
                    dao.deleteTransaction(transaction)
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
