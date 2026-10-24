package com.example.practice20261022

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.practice20261022.data.Achievement
import com.example.practice20261022.data.Item
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityAchievementsBinding
import com.example.practice20261022.databinding.ActivityAddMissionBinding
import com.example.practice20261022.databinding.ItemAchievementBinding
import com.example.practice20261022.databinding.ItemMissionChecklistBinding
import com.example.practice20261022.databinding.ItemMissionChecklistBinding.inflate
import com.example.practice20261022.utilities.UniversalAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import com.example.practice20261022.utilities.SessionManager

class AchievementsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAchievementsBinding
    private lateinit var achievementAdapter: UniversalAdapter<Achievement, ItemAchievementBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        // ⚡ 1. Count completed missions right when AchievementsActivity opens!
        val allMissions = dao.getMissionsListByUserId(userId)
        val totalMissions = allMissions.size
        val completedCount = allMissions.count { it.status.equals("Completed", ignoreCase = true) }
        val goBagCompletedCount = allMissions.count {
            it.status.equals("Completed", ignoreCase = true) &&
                    it.category.equals("Go-Bag Supply", ignoreCase = true)
        }
// ⚡ 2. Unlock any earned achievements in the DB
        if (completedCount >= 1) dao.unlockAchievement(userId, "First Responder")
        if (goBagCompletedCount >= 3) dao.unlockAchievement(userId, "Go-Bag Ready")
        if (completedCount >= 4) dao.unlockAchievement(userId, "Prepared Citizen")
        if (completedCount >= 5 && completedCount == totalMissions) dao.unlockAchievement(userId, "Quest Master")

        achievementAdapter = UniversalAdapter(
            inflate = ItemAchievementBinding::inflate,
            bind = {b, item ->
                b.imvAchievement.setImageResource(item.imgId)
                b.txvAchievementName.text = item.title
                b.txvAchievementDescription.text = item.description

                val currentProgress = if (item.title == "Go-Bag Ready") goBagCompletedCount else completedCount



                if(item.isComplete){
                    b.root.alpha = 0.6f
                    b.root.strokeWidth = 6
                    b.root.strokeColor = "#93D645".toColorInt()
                    b.lpiAchievement.max = item.goal
                    b.lpiAchievement.progress = currentProgress.coerceAtMost(item.goal)
                } else {
                    b.root.alpha = 1f
                    b.root.strokeWidth = 6
                    b.root.strokeColor = "#BA2D2D".toColorInt()
                    b.lpiAchievement.max = item.goal
                    b.lpiAchievement.progress = currentProgress.coerceAtMost(item.goal)
                }


            },
            onItemClick = {}
        )
        binding.rcvGAchievements.adapter = achievementAdapter
        binding.rcvGAchievements.layoutManager = GridLayoutManager(this, 2)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllAchievementsByUserId(userId).collect { achievement ->
                achievementAdapter.submitList(achievement)
            }

        }
    }
}