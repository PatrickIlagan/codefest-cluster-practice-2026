package com.example.backend10272026

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.backend10272026.data.Opportunity
import com.example.backend10272026.data.OpportunitySkill
import com.example.backend10272026.data.Skill
import com.example.backend10272026.data.User
import com.example.backend10272026.data.UserSkill
import com.example.backend10272026.data.dao
import com.example.backend10272026.databinding.ActivityLogInBinding
import com.example.backend10272026.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)



        lifecycleScope.launch {
            val skills = dao.getAllSkills().first()
            if (skills.isEmpty()){
                val skill1 = Skill(
                    id = 1,
                    title = "Backend"
                )

                val skill2 = Skill(
                    id = 2,
                    title = "Frontend"
                )

                val skill3 = Skill(
                    id = 3,
                    title = "Database"
                )

                val skill4 = Skill(
                    id = 4,
                    title = "Cloud"
                )

                val skill5 = Skill(
                    id = 5,
                    title = "Architectural"
                )

                val skill6 = Skill(
                    id = 6,
                    title = "Technical"
                )

                val skill7 = Skill(
                    id = 7,
                    title = "Art"
                )

                val skill8 = Skill(
                    id = 8,
                    title = "PageMaster"
                )

                val skill9 = Skill(
                    id = 9,
                    title = "System"
                )

                val skill10 = Skill(
                    id = 10,
                    title = "Networking"
                )

                val skill11 = Skill(
                    id = 11,
                    title = "Canva"
                )

                val skill12 = Skill(
                    id = 12,
                    title = "Fullstack"
                )

                val skill13 = Skill(
                    id = 13,
                    title = "Cybersecurity"
                )

                val skill14 = Skill(
                    id = 14,
                    title = "AI"
                )

                val skill15 = Skill(
                    id = 15,
                    title = "Machine Learning"
                )


                val backendSkill = dao.insertSkill(skill1)
                val frontendSkill = dao.insertSkill(skill2)
                val databaseSkill = dao.insertSkill(skill3)
                val cloudSkill = dao.insertSkill(skill4)
                val architecturalSkill = dao.insertSkill(skill5)
                val technicalSkill = dao.insertSkill(skill6)
                val artSkill = dao.insertSkill(skill7)
                val pageMasterSkill = dao.insertSkill(skill8)
                val systemSkill = dao.insertSkill(skill9)
                val networkingSkill = dao.insertSkill(skill10)
                val canvaSkill = dao.insertSkill(skill11)
                val fullstackSkill = dao.insertSkill(skill12)
                val cybersecuritySkill = dao.insertSkill(skill13)
                val AIskill = dao.insertSkill(skill14)
                val MLskill = dao.insertSkill(skill15)

                val usery = dao.getAllUsers().first()

                if (usery.isEmpty()){
                    val user = User(
                        name = "Patrick",
                        email = "johnpatbum01@gmail.com",
                        password = "123456",
                        proficiency = "Beginner"
                    )
                    val userIdfr = dao.insertUser(user)

                    val user1 = dao.getUserById(userIdfr)
                    if (user1 != null){
                        val userSkill1 = UserSkill(
                            userId = user1.id,
                            username = user1.name,
                            title = "Backend",
                            skillId = backendSkill,
                        )

                        val userSkill2 = UserSkill(
                            userId = user1.id,
                            username = user1.name,
                            title = "Frontend",
                            skillId = frontendSkill
                        )

                        val userSkill3 = UserSkill(
                            userId = user1.id,
                            username = user1.name,
                            title = "Database",
                            skillId = databaseSkill
                        )

                        dao.insertUserSkill(userSkill1)
                        dao.insertUserSkill(userSkill2)
                        dao.insertUserSkill(userSkill3)
                    }

                    val opportunitybebe = dao.getAllOpportunities().first()

                    if (opportunitybebe.isEmpty()){
                        val ins1opportunity = Opportunity(
                            title = "Backend Job",
                            organization = "Oracle",
                            category = "Entry-Level",
                            matchPercentage = 0,
                            bookmarkType = "Saved",
                            description = "Must be 67 years old and has 67 experience with the Java Language.",
                            lastTimestampSaved = 0
                        )
                        val opportunityId = dao.insertOpportunity(ins1opportunity)

                        val opportunity1 = dao.getOpportunityById(opportunityId)
                        if (opportunity1 != null) {
                            val opportunitySkill1 = OpportunitySkill(
                                opportunityId = opportunity1.id,
                                type = "Required",
                                title = "Backend",
                                skillId = backendSkill
                            )

                            val opportunitySkill2 = OpportunitySkill(
                                opportunityId = opportunity1.id,
                                type = "Required",
                                title = "Database",
                                skillId = databaseSkill
                            )

                            val opportunitySkill3 = OpportunitySkill(
                                opportunityId = opportunity1.id,
                                type = "Required",
                                title = "Cloud",
                                skillId = cloudSkill
                            )

                            val opportunitySkill4 = OpportunitySkill(
                                opportunityId = opportunity1.id,
                                type = "Preferred",
                                title = "Architectural",
                                skillId = architecturalSkill
                            )

                            val opportunitySkill5 = OpportunitySkill(
                                opportunityId = opportunity1.id,
                                type = "Preferred",
                                title = "Technical",
                                skillId = technicalSkill
                            )

                            dao.insertOpportunitySkill(opportunitySkill1)
                            dao.insertOpportunitySkill(opportunitySkill2)
                            dao.insertOpportunitySkill(opportunitySkill3)
                            dao.insertOpportunitySkill(opportunitySkill4)
                            dao.insertOpportunitySkill(opportunitySkill5)
                        }
                        val ins2opportunity = Opportunity(
                            title = "Front End Internship",
                            organization = "Canva Philippines",
                            category = "Intenrship",
                            matchPercentage = 0,
                            bookmarkType = "Rejected",
                            description = "An internship done for Canva Philippines with the likes of editing and centering a div",
                            lastTimestampSaved = 0
                        )

                        val opportunity2Id = dao.insertOpportunity(ins2opportunity)

                        val opportunity2 = dao.getOpportunityById(opportunity2Id)

                        if (opportunity2 != null){
                            val opportunitySkill6 = OpportunitySkill(
                                opportunityId = opportunity2.id,
                                type = "Required",
                                title = "Frontend",
                                skillId = frontendSkill
                            )

                            val opportunitySkill7 = OpportunitySkill(
                                opportunityId = opportunity2.id,
                                type = "Required",
                                title = "Art",
                                skillId = artSkill
                            )

                            val opportunitySkill8 = OpportunitySkill(
                                opportunityId = opportunity2.id,
                                type = "Required",
                                title = "Architectural",
                                skillId = architecturalSkill
                            )
                            val opportunitySkill9 = OpportunitySkill(
                                opportunityId = opportunity2.id,
                                type = "Preferred",
                                title = "PageMaster",
                                skillId = pageMasterSkill
                            )

                            val opportunitySkill10 = OpportunitySkill(
                                opportunityId = opportunity2.id,
                                type = "Preferred",
                                title = "Canva",
                                skillId = canvaSkill
                            )

                            dao.insertOpportunitySkill(opportunitySkill6)
                            dao.insertOpportunitySkill(opportunitySkill7)
                            dao.insertOpportunitySkill(opportunitySkill8)
                            dao.insertOpportunitySkill(opportunitySkill9)
                            dao.insertOpportunitySkill(opportunitySkill10)
                        }


                        val ins3opportunity = Opportunity(
                            title = "Database Training",
                            organization = "Microsoft Excel",
                            category = "Training",
                            matchPercentage = 0,
                            bookmarkType = "Unsaved",
                            description = "Training a database to walk, jump and run",
                            lastTimestampSaved = 0
                        )
                        val opportunity3Id = dao.insertOpportunity(ins3opportunity)


                        val opportunity3 = dao.getOpportunityById(opportunity3Id)
                        if (opportunity3 != null) {
                            val opportunitySkill11 = OpportunitySkill(
                                opportunityId = opportunity3.id,
                                type = "Required",
                                title = "Backend",
                                skillId = backendSkill
                            )

                            val opportunitySkill12 = OpportunitySkill(
                                opportunityId = opportunity3.id,
                                type = "Required",
                                title = "Database",
                                skillId = databaseSkill

                            )
                            val opportunitySkill13 = OpportunitySkill(
                                opportunityId = opportunity3.id,
                                type = "Required",
                                title = "Cloud",
                                skillId = cloudSkill
                            )
                            val opportunitySkill14 = OpportunitySkill(
                                opportunityId = opportunity3.id,
                                type = "Preferred",
                                title = "AI",
                                skillId = AIskill
                            )
                            val opportunitySkill15 = OpportunitySkill(
                                opportunityId = opportunity3.id,
                                type = "Preferred",
                                title = "Machine Learning",
                                skillId = MLskill
                            )

                            dao.insertOpportunitySkill(opportunitySkill11)
                            dao.insertOpportunitySkill(opportunitySkill12)
                            dao.insertOpportunitySkill(opportunitySkill13)
                            dao.insertOpportunitySkill(opportunitySkill14)
                            dao.insertOpportunitySkill(opportunitySkill15)
                        }



                    }
                }
            }



        }
    }
}