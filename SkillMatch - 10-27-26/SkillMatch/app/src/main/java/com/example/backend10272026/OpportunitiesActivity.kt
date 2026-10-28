package com.example.backend10272026

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Grid
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.backend10272026.data.Opportunity
import com.example.backend10272026.data.OpportunitySkill
import com.example.backend10272026.data.Skill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityOpportunitiesBinding
import com.example.backend10272026.databinding.ActivityProfileBinding
import com.example.backend10272026.databinding.ItemCardOpportunityBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding.inflate
import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import com.example.backend10272026.utilities.setupBottomNav
import kotlinx.coroutines.launch

class OpportunitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpportunitiesBinding
    private lateinit var opportunityAdapter: UniversalAdapter<Opportunity, ItemCardOpportunityBinding>

    private var rawList: List<Opportunity> = emptyList()
    private var isAscending: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_opportunities)

        val session = SessionManager(this)
        val userId = session.getUserID()

        binding = ActivityOpportunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val category = arrayOf("All","Internship", "Entry-Level", "Training", "Scholarship")
        binding.edmCategory.setSimpleItems(category)
        binding.edmCategory.setText("All", false)

        val skillNameList = arrayOf("All") + dao.getAllSkillNameList().toTypedArray()
        binding.edmSkills.setSimpleItems(skillNameList)
        binding.edmSkills.setText("All", false)

        opportunityAdapter = UniversalAdapter(
            inflate = ItemCardOpportunityBinding::inflate,
            bind = { b, item ->
                b.txvOpportunityTitle2.text = item.title
                b.txvOpportunityOrg.text = item.organization
                b.txvCategory.text = item.category
                b.txvBookmarkStatus.text = item.bookmarkType



                val opportunitySkillAdapter = UniversalAdapter<OpportunitySkill, ItemChipSkillBinding>(
                    inflate = ItemChipSkillBinding::inflate,
                    bind = { bb, item2 ->
                        bb.chpSkillItem.text = item2.title
                        bb.chpSkillItem.isClickable =false

                        lifecycleScope.launch {
                            val checkList = dao.getAllOpportunitiesSkillList(item.id)
                            val userList = dao.getAllUserSkillList(userId)

                            val totalCount = checkList.size

                            val filtered = checkList.filter { it in userList }

                            val matched = filtered.size
                            val percent = if (totalCount > 0) (matched * 100) / totalCount else 0
                            b.txvMatchPercentage2.text = "$percent% Matched"
                        }


                    },
                    onItemClick = {
                    }
                )

                b.rcvRequiredSkills.adapter = opportunitySkillAdapter
                b.rcvRequiredSkills.layoutManager = GridLayoutManager(this, 4)
                b.rcvRequiredSkills.isNestedScrollingEnabled=false

                lifecycleScope.launch {
                    dao.getAllOpportunitiesSkill(item.id).collect { item ->
                        opportunitySkillAdapter.submitList(item)
                    }
                }


            },
            onItemClick = { item ->
                val intent = Intent(this, OpportunityDetailsActivity::class.java).apply {
                    putExtra("ID", item.id)
                }
                startActivity(intent)
                finish()
            }
        )

        binding.rcvOpportunities.adapter = opportunityAdapter
        binding.rcvOpportunities.layoutManager = LinearLayoutManager(this)
        binding.rcvOpportunities.isNestedScrollingEnabled=false

        binding.btnSearchOpportunities.doAfterTextChanged { filter() }
        binding.edmSkills.doAfterTextChanged { filter() }
        binding.edmCategory.doAfterTextChanged { filter() }
        binding.btnFilterMatch.setOnClickListener {
            isAscending = !isAscending
            filter()
        }

        setupBottomNav(binding.bottomNav, R.id.menuOpportunities)


        lifecycleScope.launch {
            dao.getAllOpportunities().collect { item ->
                rawList = item
                filter()
            }
        }
    }

    private fun filter(){
        var filtered = rawList
        val query = binding.btnSearchOpportunities.text.toString().trim()
        val selectedCategory = binding.edmCategory.text.toString().trim()
        val selectedSkill = binding.edmSkills.text.toString().trim()

        if (query.isNotBlank()){
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        if (selectedCategory != "All"){
            filtered = filtered.filter {
                it.category.contains(selectedCategory, ignoreCase = true)
            }
        }

        if (selectedSkill != "All"){
            filtered = filtered.filter { opp ->
                selectedSkill in dao.getAllOpportunitiesSkillList(opp.id)
            }
        }

        filtered = if (isAscending){
            filtered.sortedBy {  it.matchPercentage }
        } else {
            filtered.sortedByDescending {  it.matchPercentage }
        }

        opportunityAdapter.submitList(filtered)
    }
}