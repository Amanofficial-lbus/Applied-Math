package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.SyllabusCatalog
import com.example.data.model.ChapterEntity
import com.example.data.model.StreakEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TopicEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        ChapterEntity::class,
        TopicEntity::class,
        StreakEntity::class,
        StudySessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chapterDao(): ChapterDao
    abstract fun topicDao(): TopicDao
    abstract fun streakDao(): StreakDao
    abstract fun studySessionDao(): StudySessionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "applied_math_tracker.db"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())

            // 1. Insert 13 Chapters
            database.chapterDao().insertAll(SyllabusCatalog.getChapters())

            // 2. Insert all 93 Topics in fresh state (0 completed, first 5 assigned to today)
            database.topicDao().insertAll(SyllabusCatalog.getTopics(today))

            // 3. Insert Starter Streak at zero/fresh state
            database.streakDao().insertOrUpdate(
                StreakEntity(
                    id = 1,
                    currentStreak = 0,
                    lastActiveDate = "",
                    freezesUsed = 0,
                    freezeShieldAvailable = true,
                    totalFreezesAvailable = 1,
                    bestStreak = 0,
                    totalXp = 0,
                    level = 1,
                    rankTitle = "Novice Scholar",
                    estimatedMasteredHours = 0.0
                )
            )
            // Study sessions start completely empty (0 sessions logged)
        }
    }
}
