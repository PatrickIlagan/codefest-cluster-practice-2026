package com.example.practice20261022

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityRankBinding
import com.example.practice20261022.utilities.SessionManager
import kotlinx.coroutines.launch

class RankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRankBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val user = dao.getUserById(userId)




        binding.btnMissionRedirect.setOnClickListener {
            startActivity(Intent(this, MissionsActivity::class.java))
            finish()
        }

        binding.btnAchievements.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
            finish()
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            dao.getAllMissionsByUserId(userId).collect { missions ->
                val currentXP = missions.filter { it.status == "Completed" }.sumOf { it.xpReward }






                binding.txvCurrentXP.text = currentXP.toString()
                if (currentXP <= 199) {
                    binding.imvRank.setImageResource(R.drawable.rank_1_scout)
                    binding.lpiRank.max = 200                  // 👈 Fixed from 400 to 200!
                    binding.lpiRank.progress = currentXP       // 0 to 199
                    binding.txvGoalXP.text = "200"
                    binding.txvRankName.text = "Citizen Scout"
                } else if (currentXP <= 399) {
                    binding.imvRank.setImageResource(R.drawable.rank_2_responder)
                    binding.lpiRank.max = 200
                    binding.lpiRank.progress = currentXP - 200 // 👈 At 200 XP = 0%, at 300 XP = 50%!
                    binding.txvGoalXP.text = "400"
                    binding.txvRankName.text = "Barangay Responder"
                } else if (currentXP <= 599) {
                    binding.imvRank.setImageResource(R.drawable.rank_3_vanguard)
                    binding.lpiRank.max = 200
                    binding.lpiRank.progress = currentXP - 400 // 👈 At 400 XP = 0%, at 500 XP = 50%!
                    binding.txvGoalXP.text = "600"
                    binding.txvRankName.text = "Storm Vanguard"
                } else {
                    binding.imvRank.setImageResource(R.drawable.rank_4_hero)
                    binding.lpiRank.max = 200
                    binding.lpiRank.progress = 200             // 👈 100% Full Bar at MAX!
                    binding.txvGoalXP.text = "MAX XP"
                    binding.txvRankName.text = "Bayan Hero"    // 👈 Also fixed "Bayan Herp" typo!
                }
            }
        }
    }
}