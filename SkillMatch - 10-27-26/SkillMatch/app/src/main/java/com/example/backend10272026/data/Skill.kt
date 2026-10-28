package com.example.backend10272026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skills_table")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
)