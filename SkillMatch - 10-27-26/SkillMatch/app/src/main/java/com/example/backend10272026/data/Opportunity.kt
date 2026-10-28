package com.example.backend10272026.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "opportunities_table")
data class Opportunity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val organization: String,
    val category: String,
    val matchPercentage: Int,
    val bookmarkType: String,
    val description: String,
    val lastTimestampSaved: Long,
)