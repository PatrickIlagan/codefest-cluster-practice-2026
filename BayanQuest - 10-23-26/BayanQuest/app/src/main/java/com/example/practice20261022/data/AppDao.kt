package com.example.practice20261022.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Delete
    fun deleteMission(mission: Mission)

    @Delete
    fun deleteResource(resource: Resource)

    @Delete
    fun deleteItem(item: Item)

    @Update
    fun updateMission(mission: Mission)

    @Update
    fun updateResource(resource: Resource)

    @Update
    fun updateItem(item: Item)

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id: Long): User?
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: Item): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAchievement(achievement: Achievement): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMission(mission: Mission): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResource(resource: Resource): Long

    @Query("SELECT * FROM missions_table WHERE id = :id")
    fun getMissionById(id: Long): Mission?

    @Query("SELECT * FROM items_table WHERE id = :id")
    fun getItemById(id: Long): Item?

    @Query("SELECT * FROM items_table WHERE missionId = :id")
    fun getItemByMissionId(id: Long): Flow<List<Item>>

    @Query("SELECT * FROM resources_table WHERE id = :id")
    fun getResourceByID(id: Long): Resource?

    @Query("SELECT * FROM users_table WHERE email = :email AND password = :password")
    fun loginUser(email: String, password: String): User?

    @Query("UPDATE achievements_table SET isComplete = 1 WHERE userId = :userId AND title = :title")
    fun unlockAchievement(userId: Long, title: String)

    @Query("SELECT * FROM missions_table WHERE userId = :id")
    fun getAllMissionsByUserId(id: Long): Flow<List<Mission>>

    @Query("SELECT * FROM missions_table WHERE userId = :id")
    fun getMissionsListByUserId(id: Long): List<Mission>

    @Query("SELECT * FROM achievements_table WHERE userId = :id")
    fun getAllAchievementsByUserId(id: Long): Flow<List<Achievement>>

    @Query("SELECT * FROM resources_table WHERE userId = :id")
    fun getAllResourcesByUserId(id: Long): Flow<List<Resource>>
}