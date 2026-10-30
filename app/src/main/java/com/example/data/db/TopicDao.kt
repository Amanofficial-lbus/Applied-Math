package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY id ASC")
    fun getAllTopicsFlow(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics ORDER BY id ASC")
    suspend fun getAllTopicsList(): List<TopicEntity>

    @Query("SELECT * FROM topics WHERE date_assigned = :date AND is_rollover = 0 ORDER BY is_completed ASC, id ASC")
    fun getTodayTargetsFlow(date: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE date_assigned = :date AND is_rollover = 0")
    suspend fun getTodayTargetsList(date: String): List<TopicEntity>

    @Query("SELECT * FROM topics WHERE is_rollover = 1 AND is_completed = 0 ORDER BY id ASC")
    fun getBacklogTopicsFlow(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE is_rollover = 1 AND is_completed = 0")
    suspend fun getBacklogTopicsList(): List<TopicEntity>

    @Query("SELECT COUNT(*) FROM topics WHERE is_completed = 1")
    fun getCompletedTopicsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics")
    fun getTotalTopicsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics")
    suspend fun getTotalTopicsCount(): Int

    @Query("SELECT * FROM topics WHERE chapter_id = :chapterId ORDER BY id ASC")
    fun getTopicsByChapter(chapterId: Int): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id LIMIT 1")
    suspend fun getTopicById(id: Int): TopicEntity?

    @Query("UPDATE topics SET is_completed = :isCompleted, completedAt = :completedAt WHERE id = :id")
    suspend fun setTopicCompletion(id: Int, isCompleted: Boolean, completedAt: Long?)

    @Query("UPDATE topics SET is_rollover = 1 WHERE date_assigned < :today AND is_completed = 0")
    suspend fun markOverdueAsRollover(today: String): Int

    @Query("UPDATE topics SET is_rollover = 0, date_assigned = :today WHERE id = :id")
    suspend fun moveTopicToToday(id: Int, today: String)

    @Query("UPDATE topics SET date_assigned = :date WHERE id IN (:ids)")
    suspend fun assignTopicsDate(ids: List<Int>, date: String)

    @Query("SELECT * FROM topics WHERE is_completed = 0 AND (date_assigned IS NULL OR date_assigned = '') AND is_rollover = 0 ORDER BY id ASC LIMIT :limit")
    suspend fun getUnassignedTopics(limit: Int): List<TopicEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)

    @Query("UPDATE topics SET is_completed = 0, is_rollover = 0, date_assigned = NULL, completedAt = NULL")
    suspend fun resetAllTopics()

    @Update
    suspend fun update(topic: TopicEntity)
}
