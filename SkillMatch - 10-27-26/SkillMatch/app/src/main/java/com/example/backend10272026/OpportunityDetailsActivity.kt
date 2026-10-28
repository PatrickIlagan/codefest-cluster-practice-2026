package com.example.backend10272026

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.backend10272026.data.OpportunitySkill
import com.example.backend10272026.data.UserSkill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityMainBinding
import com.example.backend10272026.databinding.ActivityOpportunityDetailsBinding
import com.example.backend10272026.databinding.ActivityProfileBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding.inflate
import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import kotlinx.coroutines.launch

class OpportunityDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpportunityDetailsBinding
    private lateinit var requiredAdapter: UniversalAdapter<OpportunitySkill, ItemChipSkillBinding>
    private lateinit var preferredAdapter: UniversalAdapter<OpportunitySkill, ItemChipSkillBinding>
    private lateinit var userAdapter: UniversalAdapter<UserSkill, ItemChipSkillBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOpportunityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val oppId = intent.getLongExtra("ID", -1L)
        val opportunity = dao.getOpportunityById(oppId)

        val user = dao.getUserById(userId)

        val status = arrayOf("Unsaved","Saved", "Applied", "Interviewed", "Rejected", "Offered")
        binding.edmOpportunityStatus.setSimpleItems(status)

        if (user != null && opportunity != null){
            binding.txvOpportunityOrg.text = opportunity.organization
            binding.txvOpportunityTitle.text = opportunity.title
            binding.txvCategory.text = opportunity.category
            binding.txvOpportunityDescription.text = opportunity.description

            if (opportunity.bookmarkType == "Unsaved"){
                binding.edmOpportunityStatus.setText("Unsaved", false)
            } else {
                binding.edmOpportunityStatus.setText(opportunity.bookmarkType, false)
            }

            binding.edmOpportunityStatus.doAfterTextChanged {
                val bookmark =  binding.edmOpportunityStatus.text.toString().trim()
                val newOpportunity = opportunity.copy(
                    bookmarkType = bookmark,
                    lastTimestampSaved = if (!bookmark.equals("Unsaved", ignoreCase = true)) System.currentTimeMillis() else 0
                )
                dao.updateOpportunity(newOpportunity)
                Toast.makeText(this, "Updated Bookmark to $bookmark", Toast.LENGTH_SHORT).show()
            }

            requiredAdapter = UniversalAdapter(
                inflate = ItemChipSkillBinding::inflate,
                bind = { b, item ->
                    b.chpSkillItem.text = item.title
                    b.chpSkillItem.isClickable = false


                },
                onItemClick = {

                }
            )

            binding.rcvRequiredSkills.adapter = requiredAdapter
            binding.rcvRequiredSkills.layoutManager = GridLayoutManager(this, 3)
            binding.rcvRequiredSkills.isNestedScrollingEnabled=false

            preferredAdapter = UniversalAdapter(
                inflate = ItemChipSkillBinding::inflate,
                bind = { b, item ->

                    b.chpSkillItem.text = item.title
                    b.chpSkillItem.isClickable = false



                },
                onItemClick = {

                }
            )
            binding.rcvPreferredSkills.adapter = preferredAdapter
            binding.rcvPreferredSkills.layoutManager = GridLayoutManager(this, 3)
            binding.rcvPreferredSkills.isNestedScrollingEnabled=false

            userAdapter = UniversalAdapter(
                inflate = ItemChipSkillBinding::inflate,
                bind = { b, item ->
                    b.chpSkillItem.text = item.title
                    b.chpSkillItem.isClickable = false

                    val checkList = dao.getAllOpportunitiesSkillList(opportunity.id)

                    val isMatched = checkList.any {it.equals(item.title, ignoreCase = true)}


                    if (isMatched){
                        b.chpSkillItem.isChecked = true
                        b.root.alpha = 1.0f
                        b.root.chipStrokeWidth =6f
                        b.root.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#CAED3B"))
                    } else {
                        b.chpSkillItem.isChecked = false
                        b.root.alpha = 0.7f
                        b.root.chipStrokeWidth = 6f
                        b.root.chipStrokeColor = ColorStateList.valueOf(Color.parseColor("#ED1953"))
                    }

                },
                onItemClick = {

                }
            )
            binding.rcvUserSkills.adapter = userAdapter
            binding.rcvUserSkills.layoutManager = GridLayoutManager(this, 3)
            binding.rcvUserSkills.isNestedScrollingEnabled=false


        }

        lifecycleScope.launch {
            if (opportunity != null){
                dao.getAllOpportunitiesSkill(opportunity.id).collect { item ->
                    val preferred = item.filter { it.type.equals("Preferred", ignoreCase = true ) }
                    val required = item.filter { it.type.equals("Required", ignoreCase = true) }
                    preferredAdapter.submitList(preferred)
                    requiredAdapter.submitList(required)
                }
            }
        }

        lifecycleScope.launch {
            if (user != null){
                dao.getAllUserSkill(user.id).collect { item ->
                    userAdapter.submitList(item)
                }
            }
        }

        lifecycleScope.launch {
            if (user != null && opportunity != null){
                val checkList = dao.getAllOpportunitiesSkillList(opportunity.id)
                val userList = dao.getAllUserSkillList(user.id)

                val totalCount = checkList.size

                val filtered = checkList.filter { it in userList }

                val matched = filtered.size

                val percent = if (totalCount > 0) (matched * 100) / totalCount else 0
                binding.txvMatchPercentage.text = "$percent% Matched"

                val newOpportunity = opportunity.copy(
                    matchPercentage = percent
                )
                dao.updateOpportunity(newOpportunity)
            }

        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, OpportunitiesActivity::class.java))
            finish()
        }


    }
}