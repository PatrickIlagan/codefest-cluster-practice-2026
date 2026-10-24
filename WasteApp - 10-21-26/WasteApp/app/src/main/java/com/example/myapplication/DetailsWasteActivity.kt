package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityDetailsWasteBinding
import com.example.myapplication.utilities.SessionManager

class DetailsWasteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsWasteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsWasteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val wasteId = intent.getLongExtra("WASTE_ID", -1L)

        if (wasteId != -1L){
            val waste = dao.getEntryByUserAndId(userId, wasteId)

            binding.txvWasteDetailsName.text = waste.name
            binding.txvWasteDetailsMaterial.text = waste.category
            binding.txvWasteDetailsDate.text = waste.date
            binding.txvWasteDetailsQuantity.text = waste.quantity.toString()
            binding.txvWasteDetailsInstruction.text = waste.instruction
            binding.txvWasteDetailsRemarks.text = waste.description

            binding.btnWasteShare.setOnClickListener {
                val shareText = """
                ===WASTE LOG #${waste.id}===
                Waste Name: ${waste.name}
                Material: ${waste.category}
                Quantity:  ${waste.quantity}
                Date: ${waste.date}
                Disposal Method: ${waste.instruction}
                Remarks: ${waste.description}
            """.trimIndent()
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(intent)
            }
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditWasteActivity::class.java).apply {
                putExtra("WASTE_ID", wasteId)
            }
            startActivity(intent)
            finish()
        }



        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, WasteEntriesActivity::class.java))
            finish()
        }

    }
}