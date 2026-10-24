package com.example.practice20261022

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.practice20261022.data.dao
import com.example.practice20261022.databinding.ActivityProfileBinding
import com.example.practice20261022.utilities.SessionManager
import com.example.practice20261022.utilities.setupBottomNav
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserId()

        val user = dao.getUserById(userId)

        if (user != null){
            binding.txvProfileUsername.text = user.name
            binding.txvProfileRank.text = user.rank
        }

        binding.cdvAchievementRedirect.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
            finish()
        }
        binding.cdvLogout.setOnClickListener {
            startActivity(Intent(this, LogInActivity::class.java))
            session.logout()
            finish()
        }
        binding.cdvRankRedirect.setOnClickListener {
            startActivity(Intent(this, RankActivity::class.java))
            finish()
        }

        binding.btnRank.setOnClickListener {
            startActivity(Intent(this, RankActivity::class.java))
            finish()
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
        setupBottomNav(binding.bottomNav, R.id.nav_profile)
        lifecycleScope.launch {
            dao.getAllMissionsByUserId(userId).collect { missions ->
                    val currentXP = missions.filter { it.status == "Completed" }.sumOf { it.xpReward }
                    val maxXP = 600
                    val percent = if (maxXP > 0) (currentXP * 100) / maxXP else 0
                    binding.lpiRank.max = 600
                    binding.lpiRank.progress = currentXP
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
    }
}