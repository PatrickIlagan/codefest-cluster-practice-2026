package com.example.practice20261026.data

import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "products_table",
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
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val name: String,
    val category: String,
    val sellingPrice: Double,
    val status: String,
    val quantity: Int,
    val minThresh: Int,
    val unit: String,
    val date: String,
    val timestamp: Long
)