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
import com.example.practice20261026.databinding.ActivityDetailsTransactionBinding
import com.example.practice20261026.utilities.SessionManager

class DetailsTransactionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsTransactionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val session = SessionManager(this)
        val userId = session.getUserID()

        binding = ActivityDetailsTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionId = intent.getLongExtra("ID", -1L)

        if (transactionId != -1L){
            val transaction = dao.getTransactionById(transactionId)
            if (transaction != null){
                binding.txvTransactionName.text = transaction.customerName
                binding.txvTransactionCash.text = transaction.payment.toString()
                binding.txvTransactionProduct.text = transaction.productName
                binding.txvTransactionQuantity.text = transaction.quantity.toString()
                binding.txvTransactionDueDate.text = transaction.dueDate
                binding.txvTransactionCashType.text = transaction.type
                binding.txvTotalAmount.text = transaction.totalAmount.toString()
                binding.txvTransactionType.text = transaction.type
                binding.txvNetAmount.text = transaction.netAmount.toString()
                binding.txvTransactionChange.text = transaction.change.toString()


                binding.btnEditTransaction.setOnClickListener {
                    EditTransactionSheet(transactionId).show(supportFragmentManager, "EditTransactionSheet")
                }

                binding.btnDeleteProduct.setOnClickListener {
                    dao.deleteTransaction(transaction)
                    Toast.makeText(this, "Deleted Successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, TransactionActivity::class.java))
                    finish()
                }


                binding.btnBack.setOnClickListener {
                    startActivity(Intent(this, TransactionActivity::class.java))
                    finish()
                }
            }
        }
    }
}