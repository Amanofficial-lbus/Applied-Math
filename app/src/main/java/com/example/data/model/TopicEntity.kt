package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "chapter_id") val chapterId: Int,
    val name: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "is_rollover") val isRollover: Boolean = false,
    @ColumnInfo(name = "date_assigned") val dateAssigned: String? = null,
    val code: String = "",
    val chapterName: String = "",
    val estimatedMinutes: Int = 25,
    val notes: String = "",
    val completedAt: Long? = null
)
