package com.example.backend10282026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods_table")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String,
    val price: Double,
    val description: String,
    val image: Int
)