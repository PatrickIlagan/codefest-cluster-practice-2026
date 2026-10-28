package com.example.backend10282026.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "orderItems_table",
    foreignKeys = [
        ForeignKey(
            entity = Order::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ], indices = [Index("orderId")]
)
data class OrderItem (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val orderId: Long,
    val foodName: String,
    val category: String,
    val quantity: Int,
    val image: Int,
    val price: Double
)