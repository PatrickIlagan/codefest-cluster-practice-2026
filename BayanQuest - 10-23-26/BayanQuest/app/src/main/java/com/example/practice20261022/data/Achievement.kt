package com.example.practice20261022.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "achievements_table",
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
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val description: String,
    val imgId: Int,
    val isComplete: Boolean = false,
    val goal: Int,
    val progress: Int
)