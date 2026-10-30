package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val code: String,
    val iconName: String,
    val colorHex: String,
    val description: String,
    val totalTopics: Int
)
