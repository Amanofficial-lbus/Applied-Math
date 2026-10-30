package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_sessions")
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val durationMinutes: Int,
    val sessionType: String, // "Pomodoro 25m", "Quick Drill", "Overdue Cram"
    val topicName: String,
    val xpEarned: Int,
    val timestamp: Long = System.currentTimeMillis()
)
