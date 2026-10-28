package com.example.backend10272026.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "opportunitySkills_table",
    foreignKeys = [
        ForeignKey(
            entity = Opportunity::class,
            parentColumns = ["id"],
            childColumns = ["opportunityId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ], indices = [Index("opportunityId")]
)
data class OpportunitySkill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val opportunityId: Long,
    val type: String,
    val title: String,
    val skillId: Long,
)