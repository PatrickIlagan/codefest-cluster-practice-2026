package com.example.backend10282026.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cartItems_table",
    foreignKeys = [
        ForeignKey(
            entity = Food::class,
            parentColumns = ["id"],
            childColumns = ["foodId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ], indices = [Index("foodId")]
)
data class CartItem (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val foodId: Long,
    val userId: Long,
    val quantity: Int,
    val price: Double,
)