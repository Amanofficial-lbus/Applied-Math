package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "current_streak") val currentStreak: Int = 0,
    @ColumnInfo(name = "last_active_date") val lastActiveDate: String = "",
    @ColumnInfo(name = "freezes_used") val freezesUsed: Int = 0,
    val freezeShieldAvailable: Boolean = true,
    val totalFreezesAvailable: Int = 1,
    val bestStreak: Int = 0,
    val totalXp: Int = 0,
    val level: Int = 1,
    val rankTitle: String = "Novice Scholar",
    val estimatedMasteredHours: Double = 0.0
)
