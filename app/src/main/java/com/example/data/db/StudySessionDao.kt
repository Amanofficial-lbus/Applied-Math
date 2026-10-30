package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions")
    suspend fun getAllSessionsList(): List<StudySessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: StudySessionEntity): Long

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalMinutesFlow(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM study_sessions WHERE date = :date")
    fun getSessionCountForDate(date: String): Flow<Int>

    @Query("DELETE FROM study_sessions")
    suspend fun clearAllSessions()
}
