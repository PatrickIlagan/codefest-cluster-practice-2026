package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.data.dao
import com.example.myapplication.databinding.ActivityDetailsLocationBinding
import androidx.core.net.toUri
import com.example.myapplication.utilities.sendNotif

class DetailsLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailsLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val locationId = intent.getLongExtra("ID", -1L)

        if (locationId != -1L){
            val location = dao.getLocationById(locationId)

            binding.txvLocationDetailsName.text = location.name
            binding.txvLocationDetailsMaterial.text = location.category
            binding.txvLocationDetailsStart.text = location.startSched
            binding.txvLocationDetailsEnd.text = location.endSched
            binding.txvLocationDetailsAddress.text = location.address
            binding.txvLocationDetailsRemarks.text = location.remarks

            binding.btnLocationSearch.setOnClickListener {
                sendNotif(
                    "Newly Deleted ${location.name}",
                    "Your Location ID: ${location.id} has been deleted."
                )
//                val mapUrl = "https://www.google.com/maps/search/api=1&query=" + Uri.encode(
//                    location.name
//                )
//                startActivity(Intent(Intent.ACTION_VIEW, mapUrl.toUri()))
            }
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, EditLocationActivity::class.java).apply {
                putExtra("ID", locationId)
            }
            startActivity(intent)
            finish()
        }



        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LocationEntriesActivity::class.java))
            finish()
        }

    }
}
