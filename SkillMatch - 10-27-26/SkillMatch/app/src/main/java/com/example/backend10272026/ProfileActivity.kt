package com.example.backend10272026

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.backend10272026.data.OpportunitySkill
import com.example.backend10272026.data.Skill
import com.example.backend10272026.data.User
import com.example.backend10272026.data.UserSkill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityMainBinding
import com.example.backend10272026.databinding.ActivityProfileBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import com.example.backend10272026.utilities.setupBottomNav
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var skillAdapter: UniversalAdapter<UserSkill, ItemChipSkillBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val user = dao.getUserById(userId)

        if (user != null){
            binding.txvEmail.text = user.email
            binding.txvUsername.text = user.name


            skillAdapter = UniversalAdapter(
                inflate = ItemChipSkillBinding::inflate,
                bind = { b, item ->

                    b.chpSkillItem.text = item.title

                    b.chpSkillItem.isClickable = false



                },
                onItemClick = {

                }
            )

            binding.rcvUserSkills.adapter = skillAdapter
            binding.rcvUserSkills.layoutManager = GridLayoutManager(this, 3)
            binding.rcvUserSkills.isNestedScrollingEnabled=false

            binding.btnEditProfile.setOnClickListener {
                startActivity(Intent(this, EditProfileActivity::class.java))
                finish()
            }

            lifecycleScope.launch {
                dao.getAllUserSkill(userId).collect { item ->
                    skillAdapter.submitList(item)
                }
            }
        }

        setupBottomNav(binding.bottomNav, R.id.menuUser)

    }
}