package com.example.backend10272026

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.example.backend10272026.databinding.ActivityBookmarksBinding
import com.example.backend10272026.databinding.ActivityMainBinding
import com.example.backend10272026.databinding.ItemCardOpportunityBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding.inflate
import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import com.example.backend10272026.utilities.setupBottomNav
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class BookmarksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var bookmarkAdapter: UniversalAdapter<Opportunity, ItemCardOpportunityBinding>
    private var rawList: List<Opportunity> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        bookmarkAdapter = UniversalAdapter(
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
                b.rcvRequiredSkills.layoutManager = GridLayoutManager(this, 3)
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

        binding.rcvOpportunities.adapter = bookmarkAdapter
        binding.rcvOpportunities.layoutManager = LinearLayoutManager(this)
        binding.rcvOpportunities.isNestedScrollingEnabled=false

        binding.edtSearchBookmarks.doAfterTextChanged { filter() }
        binding.cpgBookmarkStatus.setOnCheckedChangeListener { _, _ -> filter()  }


        lifecycleScope.launch {
            dao.getAllOpportunities().collect { item ->
                rawList = item
                filter()
            }
        }

        setupBottomNav(binding.bottomNav, R.id.menuSaved)
    }
    private fun filter(){
        val status = arrayOf("Saved", "Applied", "Interviewed", "Rejected", "Offered")
        val query = binding.edtSearchBookmarks.text.toString().trim()
        val selectedChip = findViewById<Chip>(binding.cpgBookmarkStatus.checkedChipId)?.text?.toString()

        var filtered = rawList

        filtered = filtered.filter { it.bookmarkType in status }

        if (query.isNotBlank()){
            filtered = filtered.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }

        if (selectedChip != null){
            filtered = filtered.filter {
                it.bookmarkType.contains(selectedChip, ignoreCase = true)
            }
        }

        bookmarkAdapter.submitList(filtered)
    }
}