package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StreakEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE id = 1 LIMIT 1")
    fun getStreakFlow(): Flow<StreakEntity?>

    @Query("SELECT * FROM streaks WHERE id = 1 LIMIT 1")
    suspend fun getStreak(): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: StreakEntity)

    @Update
    suspend fun update(streak: StreakEntity)
}
