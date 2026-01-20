package com.app.shamba_bora.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Entity for storing saved farm advice locally
 */
@Entity(tableName = "saved_advice")
data class SavedAdvice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val overallAssessment: String?,
    val strengths: String, // JSON array as string
    val weaknesses: String, // JSON array as string
    val recommendations: String, // JSON array as string
    val bestPractices: String, // JSON array as string
    val cropOptimizationAdvice: String?,
    val investmentAdvice: String?,
    val timestamp: Long = System.currentTimeMillis()
)
