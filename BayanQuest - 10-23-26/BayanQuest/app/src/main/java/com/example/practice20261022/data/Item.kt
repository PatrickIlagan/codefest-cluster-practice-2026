package com.example.practice20261022.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "items_table",
    foreignKeys = [
        ForeignKey(
            entity =Mission::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("missionId")]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val missionId: Long,
    val description: String,
    val isChecked: Boolean = false
)