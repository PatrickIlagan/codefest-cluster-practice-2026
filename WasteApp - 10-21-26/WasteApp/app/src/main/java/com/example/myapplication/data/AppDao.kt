package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User): Long

    @Insert
    fun insertEntry(entry: Entry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: Location): Long

    @Update
    fun updateEntry(entry: Entry)

    @Update
    fun updateLocation(location: Location)

    @Query("SELECT * FROM users_table WHERE id = :id")
    fun getUserById(id: Long): User?

    @Query("SELECT * FROM users_table WHERE password = :password AND email = :email")
    fun loginUser(email: String, password: String): User?

    @Delete
    fun deleteEntry(entry: Entry)

    @Delete
    fun deleteLocation(location: Location)

    @Query("SELECT * FROM entries_table")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM locations_table")
    fun getAllLocations(): Flow<List<Location>>

    @Query("SELECT * FROM locations_table")
    fun getAllLocationsList(): List<Location>

    @Query("SELECT * FROM entries_table WHERE userId = :id")
    fun getAllEntriesByUser(id: Long): Flow<List<Entry>>

    @Query("SELECT * FROM entries_table WHERE userId = :userId AND id = :id")
    fun getEntryByUserAndId(userId: Long, id: Long): Entry

    @Query("SELECT * FROM locations_table WHERE id = :id")
    fun getLocationById(id: Long): Location

    @Query("SELECT * FROM entries_table WHERE id = :id")
    fun getEntryById(id: Long): Entry



}
