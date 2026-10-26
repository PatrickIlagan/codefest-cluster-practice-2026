package com.example.practice20261026.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "transactions_table",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,

        )
    ], indices = [Index("productId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val customerName: String,
    val productId: Long,
    val productName: String,
    val type: String,
    val quantity: Int,
    val totalAmount: Double,
    val netAmount: Double,
    val change: Double,
    val payment: Double,
    val dueDate: String,
    val dueDateTimestamp: Long,
    val currentDate: String,
    val timestamp: Long
)