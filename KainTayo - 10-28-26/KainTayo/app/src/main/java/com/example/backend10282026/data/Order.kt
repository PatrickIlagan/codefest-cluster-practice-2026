package com.example.backend10282026.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "orders_table",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ], indices = [Index("userId")]
)
data class Order (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val customerName: String,
    val notes: String,
    val total: Double,
    val orderType: String,
    val timestamp: Long,
    val date: String
)