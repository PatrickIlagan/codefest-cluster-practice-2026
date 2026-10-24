package com.example.practice20261022

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.practice20261022.data.Item
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityResourceDetailsBinding
import com.example.practice20261022.databinding.ItemMissionChecklistBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import androidx.core.net.toUri

class ResourceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResourceDetailsBinding
    private lateinit var itemAdapter: UniversalAdapter<Item, ItemMissionChecklistBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityResourceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val resourceId = intent.getLongExtra("ID", -1L)

        if (resourceId != -1L){
            val resource = dao.getResourceByID(resourceId)
            if (resource != null){
                binding.txvResourceName.text = resource.name
                binding.txvResourceCategory.text = resource.category
                binding.txvResourceContact.text = resource.contactNumber
                binding.txvAddress.text = resource.address
                binding.txvScheduleStart.text = resource.start
                binding.txvScheduleEnd.text = resource.end
                binding.txvResourceRemarks.text = resource.remarks

                binding.btnDeleteResourceDetail.setOnClickListener {
                    dao.deleteResource(resource)
                    Toast.makeText(this, "Resource ${resource.name} has been deleted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, ResourcesActivity::class.java))
                    finish()
                }

                binding.btnAddress.setOnClickListener {
                    val mapUrl = "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(
                        resource.name
                    )
                    startActivity(Intent(Intent.ACTION_VIEW, mapUrl.toUri()))

                }


            }



        }



        binding.btnEditDetailsResource.setOnClickListener {
            EditResourceSheet(resourceId).show(supportFragmentManager, "ResourceSheet")
        }



        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ResourcesActivity::class.java))
            finish()
        }
    }
}