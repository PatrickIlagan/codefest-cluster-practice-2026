package com.example.backend10272026

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.backend10272026.data.Skill
import com.example.backend10272026.data.UserSkill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityEditProfileBinding
import com.example.backend10272026.databinding.ActivityMainBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding
import com.example.backend10272026.databinding.ItemChipSkillBinding.inflate
import com.example.backend10272026.utilities.SessionManager
import com.example.backend10272026.utilities.UniversalAdapter
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var skillAdapter: UniversalAdapter<Skill, ItemChipSkillBinding>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val session = SessionManager(this)
        val userId = session.getUserID()

        val user = dao.getUserById(userId)

        val category = arrayOf("Beginner", "Intermediate", "Advanced")
        binding.edmUserProficiency.setSimpleItems(category)

        if (user != null){
            binding.edtEmail.setText(user.email)
            binding.edtUsername.setText(user.name)
            binding.edmUserProficiency.setText(user.proficiency, false)

            skillAdapter = UniversalAdapter(
                inflate = ItemChipSkillBinding::inflate,
                bind = { b, item ->
                    b.chpSkillItem.text = item.title
                    b.chpSkillItem.isClickable = true
                    b.chpSkillItem.isCheckable = true

                    val userSkills: List<String> = dao.getAllUserSkillList(userId)

                    // Check whether this skill belongs to the user
                    val isMatched = item.title in userSkills

                    b.chpSkillItem.isChecked = isMatched

                    b.chpSkillItem.setOnCheckedChangeListener { _, isChecked ->
                        if(isChecked){
                            b.root.alpha = 1f
                            b.chpSkillItem.isChecked = true
                            val userSkill = UserSkill(
                                userId = user.id,
                                username = user.name,
                                title = item.title,
                                skillId = item.id,
                            )
                            dao.insertUserSkill(userSkill)



                        }

                        if (!isChecked){
                            b.chpSkillItem.isChecked = false
                            b.root.alpha = 0.8f
                            val deleteId = item.id
                            dao.deleteUserSkillBySkillId(deleteId)
                        }
                    }

                    binding.btnClearSkills.setOnClickListener {
                        dao.deleteAllUserSkills()
                        b.chpSkillItem.isChecked = false
                        b.root.alpha = 0.8f
                    }
                },
                onItemClick = {

                }
            )

            binding.rcvSkills.adapter = skillAdapter
            binding.rcvSkills.layoutManager = GridLayoutManager(this, 3)
            binding.rcvSkills.isNestedScrollingEnabled=false

            binding.btnSaveProfile.setOnClickListener {
                val email = binding.edtEmail.text.toString().trim()
                val username = binding.edtUsername.text.toString().trim()
                val proficiency = binding.edmUserProficiency.text.toString().trim()

                if (email.isBlank()){
                    binding.edtEmail.error = "Email Missing"
                    binding.edtEmail.requestFocus()
                    return@setOnClickListener
                }
                if (username.isBlank()){
                    binding.edtUsername.error = "Username Missing"
                    binding.edtUsername.requestFocus()
                    return@setOnClickListener
                }
                if (proficiency.isBlank()){
                    binding.edmUserProficiency.error = "Profiency Missing"
                    binding.edmUserProficiency.requestFocus()
                    return@setOnClickListener
                }

                val user = user.copy(
                    email = email,
                    name = username,
                    proficiency = proficiency
                )
                dao.updateUser(user)
                Toast.makeText(this, "User Updated Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            lifecycleScope.launch {
                dao.getAllSkills().collect { item ->
                    skillAdapter.submitList(item)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }


    }
}