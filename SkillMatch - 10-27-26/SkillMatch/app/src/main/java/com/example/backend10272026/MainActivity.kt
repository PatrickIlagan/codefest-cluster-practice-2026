package com.example.backend10272026

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.backend10272026.data.Opportunity
import com.example.backend10272026.data.OpportunitySkill
import com.example.backend10272026.data.Skill
import com.example.backend10272026.data.User
import com.example.backend10272026.data.UserSkill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityMainBinding
import com.example.backend10272026.databinding.ItemCardOpportunityBinding

import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.databinding.ItemPagerAdsBinding

import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import com.example.backend10272026.utilities.setupBottomNav
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var opportunityAdapter: UniversalAdapter<Opportunity, ItemCardOpportunityBinding>
    private lateinit var recentlyAdapter: UniversalAdapter<Opportunity, ItemCardOpportunityBinding>
    private lateinit var bannerAdapter: UniversalAdapter<Int, ItemPagerAdsBinding>
    var isUserSwiping = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val session = SessionManager(this)
        session.login(1, "Patrick", "Beginner")

        val userId = session.getUserID()
        val user = dao.getUserById(userId)
        if (user != null){
            binding.txvWelcome.text = "Welcome, ${user.name}"

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

            binding.rcvTopOpportunities.adapter = opportunityAdapter
            binding.rcvTopOpportunities.layoutManager = LinearLayoutManager(this)
            binding.rcvTopOpportunities.isNestedScrollingEnabled=false

            recentlyAdapter = UniversalAdapter(
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

            binding.rcvRecentlySaved.adapter = recentlyAdapter
            binding.rcvRecentlySaved.layoutManager = LinearLayoutManager(this)
            binding.rcvRecentlySaved.isNestedScrollingEnabled=false

            binding.crdBookmarks.setOnClickListener {
                startActivity(Intent(this, BookmarksActivity::class.java))
                finish()
            }

            binding.crdProfile.setOnClickListener {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            binding.crdOpportunities.setOnClickListener {
                startActivity(Intent(this, OpportunitiesActivity::class.java))
                finish()
            }


            binding.btnOpportunities.setOnClickListener {
                startActivity(Intent(this, OpportunitiesActivity::class.java))
                finish()
            }

            binding.btnBookmarks.setOnClickListener {
                startActivity(Intent(this, BookmarksActivity::class.java))
                finish()
            }

            lifecycleScope.launch {
                dao.getAllOpportunities().collect {  list ->
                    val filtered = list.sortedByDescending { it.lastTimestampSaved }.take(2)
                    recentlyAdapter.submitList(filtered)
                }
            }

            lifecycleScope.launch {
                dao.getAllOpportunities().collect { list ->
                    val filtered = list.sortedByDescending { it.matchPercentage }.take(2)
                    opportunityAdapter.submitList(filtered)
                }
            }

            lifecycleScope.launch {
                dao.getAllOpportunities().collect { list ->
                    val totalCount = list.size
                    binding.txvTotalOpportunities.text = totalCount.toString()

                    val savedTotal = list.count {!it.bookmarkType.equals("Unsaved", ignoreCase = true)}
                    binding.txvSavedOpportunities.text = savedTotal.toString()
                }
            }

            setupBottomNav(binding.bottomNav, R.id.menuHome)
        }

        bannerAdapter = UniversalAdapter(
            inflate = ItemPagerAdsBinding::inflate,
            bind = {b, cardType ->
                b.frm1.isVisible = (cardType == 0)
                b.frm2.isVisible = (cardType == 1)
                b.frm3.isVisible = (cardType == 2)
                b.frm4.isVisible = (cardType == 3)
            }
        )

        binding.pagerBento.adapter = bannerAdapter

        TabLayoutMediator(binding.tabDots, binding.pagerBento){ tabs, _ ->
            tabs.text = "\u2b24"
        }.attach()

        binding.pagerBento.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrollStateChanged(state: Int) {
                isUserSwiping = (state == ViewPager2.SCROLL_STATE_DRAGGING)
            }
        })

        bannerAdapter.submitList(listOf(0,1,2,3))

        lifecycleScope.launch {
            if (isActive){
                delay(3500)
                if(!isUserSwiping && bannerAdapter.itemCount > 0) {
                    val nextItem = (binding.pagerBento.currentItem + 1) % bannerAdapter.itemCount
                    binding.pagerBento.setCurrentItem(nextItem, true)
                }
            }
        }
    }
}