package com.example.backend10272026.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert
    fun insertOpportunity(opportunity: Opportunity): Long

    @Insert
    fun insertUser(user: User): Long

    @Insert
    fun insertUserSkill(userSkill: UserSkill)

    @Insert
    fun insertOpportunitySkill(opportunitySkill: OpportunitySkill)

    @Update
    fun updateUser(user: User)

    @Update
    fun updateOpportunity(opportunity: Opportunity)

    @Update
    fun updateUserSkill(userSkill: UserSkill)

    @Query("DELETE FROM userSkills_table WHERE skillId = :id")
    fun deleteUserSkillBySkillId(id: Long)

    @Query("SELECT * FROM users_table WHERE id =:id")
    fun getUserById(id: Long): User?

    @Query("SELECT * FROM opportunities_table WHERE id =:id")
    fun getOpportunityById(id: Long): Opportunity?

    @Query("SELECT * FROM users_table")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM opportunities_table")
    fun getAllOpportunities(): Flow<List<Opportunity>>

    @Query("SELECT * FROM skills_table")
    fun getAllSkills(): Flow<List<Skill>>

    @Query("DELETE FROM userSkills_table")
    fun deleteAllUserSkills()

    @Query("SELECT title FROM skills_table")
    fun getAllSkillNameList(): List<String>

    @Query("SELECT title FROM opportunitySkills_table WHERE opportunityId = :oppId")
    fun getAllOpportunitiesSkillList(oppId: Long): List<String>

    @Query("SELECT * FROM opportunitySkills_table WHERE opportunityId = :oppId")
    fun getAllOpportunitiesSkill(oppId: Long): Flow<List<OpportunitySkill>>

    @Query("SELECT title FROM userSkills_table WHERE userId = :id")
    fun getAllUserSkillList(id: Long): List<String>

    @Query("SELECT * FROM userSkills_table WHERE userId = :id")
    fun getAllUserSkill(id: Long): Flow<List<UserSkill>>



    @Insert
    fun insertSkill(skill: Skill): Long



}