package com.example.practice20261022.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "missions_table",
    foreignKeys = [
        ForeignKey(
            entity =User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("userId")]
    )
data class Mission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val category: String,
    val xpReward: Int,
    val status: String,
    val date: String,
    val timestamp: Long,
    val description: String,
    val progress: Int,
    val goal: Int,
)