 package com.example.practice20261022

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.practice20261022.data.Achievement
import com.example.practice20261022.data.Mission
import com.example.practice20261022.data.Resource
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityMainBinding
import com.example.practice20261022.databinding.ActivityMissionsBinding
import com.example.practice20261022.databinding.ItemMissionBinding
import com.example.practice20261022.databinding.ItemPagerContentsBinding
import com.example.practice20261022.databinding.ItemResourceBinding
import com.example.practice20261022.databinding.ItemResourceBinding.inflate
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.UniversalAdapter
import com.example.practice20261022.utilities.setupBottomNav
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

 class MainActivity : AppCompatActivity() {
     private lateinit var bannerAdapter: UniversalAdapter<Pair<Int, List<Mission>>, ItemPagerContentsBinding>
     private lateinit var binding: ActivityMainBinding
     private lateinit var missionAdapter: UniversalAdapter<Mission, ItemMissionBinding>
     private lateinit var resourceAdapter: UniversalAdapter<Resource, ItemResourceBinding>
     private var isAscending: Boolean = false

     private var rawMissions: List<Mission> = emptyList()
     private var isUserSwiping = false
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         setContentView(binding.root)
         enableEdgeToEdge()

         val session = SessionManager(this)
         val userId = session.getUserId()


         resourceAdapter = UniversalAdapter(
             inflate = ItemResourceBinding::inflate,
             bind = {b, item ->
                 b.txvResourceName.text = item.name
                 b.txvResourceCategory.text = item.category
                 b.txvResourceContact.text = item.contactNumber
                 b.txvResourceSchedule.text = "${item.start} - ${item.end}"

                 b.btnEditResource.setOnClickListener {
                     EditResourceSheet(item.id).show(supportFragmentManager, "EditResourceSheet")
                 }

                 b.btnDeleteResource.setOnClickListener {
                     dao.deleteResource(item)

                     Snackbar.make(binding.root, "Mission ${item.name} Deleted", Snackbar.LENGTH_LONG)
                         .setAction("UNDO") {
                             dao.insertResource(item)
                         }
                         .show()

                 }
             },
             onItemClick = { item ->
                 val intent = Intent(this, ResourceDetailsActivity::class.java).apply {
                     putExtra("ID", item.id)
                 }
                 startActivity(intent)
                 finish()
             }
         )

         binding.rcvResourcesDash.adapter = resourceAdapter
         binding.rcvResourcesDash.layoutManager = LinearLayoutManager(this)
         binding.rcvResourcesDash.isNestedScrollingEnabled = false

         missionAdapter = UniversalAdapter(
             inflate = ItemMissionBinding::inflate,
             bind = {b, item ->
                 b.txvMissionItemName.text = item.title
                 b.txvMissionItemCategory.text = item.category
                 b.txvMissionItemStatus.text = item.status
                 b.txvMissionItemDate.text = item.date

                 b.btnEditMission.setOnClickListener {
                     EditMissionSheet(item.id).show(supportFragmentManager, "EditMissionSheet")
                 }

                 b.btnDeleteMission.setOnClickListener {
                     dao.deleteMission(item)

                     Snackbar.make(binding.root, "Mission ${item.title} Deleted", Snackbar.LENGTH_LONG)
                         .setAction("UNDO") {
                             dao.insertMission(item)
                         }
                         .show()

                 }

                 if (item.status.equals("Completed", ignoreCase = true)){
                     b.root.alpha = 0.5f
                     b.root.strokeWidth = 6
                     b.root.strokeColor = "#93D645".toColorInt()
                 } else {
                     b.root.alpha = 1f
                     b.root.strokeWidth = 6
                     b.root.strokeColor = "#BA2D2D".toColorInt()
                 }
             },
             onItemClick = { item ->
                 val intent = Intent(this, MissionDetailsActivity::class.java).apply {
                     putExtra("ID", item.id)
                 }
                 startActivity(intent)
                 finish()
             }
         )

         binding.rcvMissionDash.adapter = missionAdapter
         binding.rcvMissionDash.layoutManager = LinearLayoutManager(this)
         binding.rcvMissionDash.isNestedScrollingEnabled=false

         bannerAdapter = UniversalAdapter(
             inflate = ItemPagerContentsBinding::inflate,
             bind = {b, (cardType, mission) ->
                 b.layoutCardOne.isVisible = (cardType == 0)
                 b.layoutCardTwo.isVisible = (cardType == 1)
                 b.layoutCardThree.isVisible = (cardType == 2)
                 b.layoutCardFour.isVisible = (cardType == 3)

                 val max = mission.sumOf { it.goal }
                 val progress = mission.sumOf { it.progress }
                 val percent = if (max > 0) (progress * 100) / max else 0
                 val status = if (max > 0 && progress == max) "Completed" else "Pending"

                 when (cardType){
                     0 -> {




                             b.lpiGo.max = max
                             b.lpiGo.progress = progress
                             b.txvPercentGo.text = "${percent}%"
                             b.txvStatusGo.text = status

                     }
                     1 -> {




                             b.lpi2.max = max
                             b.lpi2.progress = progress
                             b.txvPercent2.text = "${percent}%"
                             b.txvStatus2.text = status

                     }
                     2 -> {


                             b.lpi3.max = max
                             b.lpi3.progress = progress
                             b.txvPercent3.text = "${percent}%"
                             b.txvStatus3.text = status

                     }
                     3 -> {

                             b.lpi4.max = max
                             b.lpi4.progress = progress
                             b.txvPercent4.text = "${percent}%"
                             b.txvStatus4.text = status

                     }
                 }


             }
         )
         binding.pagerBento.adapter = bannerAdapter

         TabLayoutMediator(binding.tabDots, binding.pagerBento){ tab, _ ->
             tab.text = "\u2b24"
         }.attach()

         binding.pagerBento.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
             override fun onPageScrollStateChanged(state: Int) {
                 isUserSwiping = (state == ViewPager2.SCROLL_STATE_DRAGGING)
             }
         })



         binding.btnMissionNext.setOnClickListener {
            startActivity(Intent(this, MissionsActivity::class.java))
             finish()
         }

         binding.btnResourceNext.setOnClickListener {
             startActivity(Intent(this, ResourcesActivity::class.java))
             finish()
         }

         binding.cdvAchievement.setOnClickListener {
             startActivity(Intent(this, AchievementsActivity::class.java))
             finish()
         }

         binding.cdvResource.setOnClickListener {
             startActivity(Intent(this, ResourcesActivity::class.java))
             finish()
         }

         binding.cdvMission.setOnClickListener {
             startActivity(Intent(this, MissionsActivity::class.java))
             finish()
         }

         lifecycleScope.launch {
             while (isActive){
                 delay(3500)
                 if(!isUserSwiping && bannerAdapter.itemCount > 0) {
                     val nextItem = (binding.pagerBento.currentItem + 1) % bannerAdapter.itemCount
                     binding.pagerBento.setCurrentItem(nextItem, true)
                 }
             }
         }

         lifecycleScope.launch {
             dao.getAllMissionsByUserId(userId).collect { missions ->
                 val goBagMission = missions.filter { it.category.equals("Go-Bag Supply", ignoreCase = true) }
                 val householdMission = missions.filter { it.category.equals("Household Prep", ignoreCase = true) }
                 val evacuationMission = missions.filter { it.category.equals("Evacuation", ignoreCase = true) }
                 val safetyGuideMission = missions.filter { it.category.equals("Safety Guide", ignoreCase = true) }
                 bannerAdapter.submitList(listOf(0 to goBagMission, 1 to householdMission, 2 to evacuationMission, 3 to safetyGuideMission))
             }

         }

         lifecycleScope.launch {
             dao.getAllMissionsByUserId(userId).collect { mission ->
                 val filteredMission = mission.sortedByDescending { it.timestamp }.take(3)
                 missionAdapter.submitList(filteredMission)
             }
         }
         lifecycleScope.launch {
             dao.getAllResourcesByUserId(userId).collect { resources ->
                 val filteredResource = resources.sortedBy { it.start }.take(3)
                 resourceAdapter.submitList(filteredResource)
             }
         }

         binding.btnRank.setOnClickListener {
             startActivity(Intent(this, RankActivity::class.java))
             finish()
         }

         binding.btnProfile.setOnClickListener {
             startActivity(Intent(this, ProfileActivity::class.java))
             finish()
         }

         setupBottomNav(binding.bottomNav, R.id.nav_home)

         lifecycleScope.launch {
             dao.getAllMissionsByUserId(userId).collect { missions ->
                 val currentXP = missions.filter { it.status == "Completed" }.sumOf { it.xpReward }

                 binding.txvXP.text = currentXP.toString()

                 if (currentXP <= 199){
                     binding.btnRank.setImageResource(R.drawable.rank_1_scout)
                 } else if (currentXP <= 399){
                     binding.btnRank.setImageResource(R.drawable.rank_2_responder)
                 } else if (currentXP <= 599){
                     binding.btnRank.setImageResource(R.drawable.rank_3_vanguard)
                 }else {
                     binding.btnRank.setImageResource(R.drawable.rank_4_hero)
                 }
             }
         }

         lifecycleScope.launch {
             val existingAchievements = dao.getAllAchievementsByUserId(userId).first()

             // 2. Check if DB is empty for this user
             if (existingAchievements.isEmpty()) {
                 val defaultAchievements = listOf(
                     Achievement(
                         id = 0L, // 0 lets Room auto-generate the primary key
                         userId = userId,
                         title = "First Responder",
                         description = "Complete your first preparedness mission (1 or more completed missions).",
                         imgId = R.drawable.badge_first_responder,
                         goal = 1,
                         progress = 0,
                         isComplete = false
                     ),
                     Achievement(
                         id = 0L,
                         userId = userId,
                         title = "Go-Bag Ready",
                         description = "Complete at least 3 missions under the \"Go-Bag Supply\" category.",
                         imgId = R.drawable.badge_gobag_ready,
                         goal = 3,
                         progress = 0,
                         isComplete = false
                     ),
                     Achievement(
                         id = 0L,
                         userId = userId,
                         title = "Prepared Citizen",
                         description = "Complete at least 4 missions in total OR reach 50% Overall Readiness.",
                         imgId = R.drawable.badge_prepared_citizen,
                         goal = 4,
                         progress = 0,
                         isComplete = false
                     ),
                     Achievement(
                         id = 0L,
                         userId = userId,
                         title = "Quest Master",
                         description = "Complete 100% of all available missions (minimum of 5 missions).",
                         imgId = R.drawable.badge_quest_master,
                         goal = 5,
                         progress = 0,
                         isComplete = false
                     )
                 )

                 // 3. Insert items into Room
                 defaultAchievements.forEach { item ->
                     dao.insertAchievement(item)
                 }
             }
         }
     }


 }
