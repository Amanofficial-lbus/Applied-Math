package com.example.data

import com.example.data.db.AppDatabase
import com.example.data.model.ChapterEntity
import com.example.data.model.FormulaItem
import com.example.data.model.StreakEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TopicEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.max

class StudyRepository(private val db: AppDatabase) {

    private val chapterDao = db.chapterDao()
    private val topicDao = db.topicDao()
    private val streakDao = db.streakDao()
    private val sessionDao = db.studySessionDao()

    val allChapters: Flow<List<ChapterEntity>> = chapterDao.getAllChapters()
    val allTopics: Flow<List<TopicEntity>> = topicDao.getAllTopicsFlow()
    val streakFlow: Flow<StreakEntity?> = streakDao.getStreakFlow()
    val allSessions: Flow<List<StudySessionEntity>> = sessionDao.getAllSessionsFlow()
    val totalMinutesFlow: Flow<Int?> = sessionDao.getTotalMinutesFlow()
    val completedTopicsCountFlow: Flow<Int> = topicDao.getCompletedTopicsCountFlow()
    val totalTopicsCountFlow: Flow<Int> = topicDao.getTotalTopicsCountFlow()

    fun getTodayTargets(today: String): Flow<List<TopicEntity>> =
        topicDao.getTodayTargetsFlow(today)

    fun getBacklogTopics(): Flow<List<TopicEntity>> =
        topicDao.getBacklogTopicsFlow()

    fun getFormulas(): List<FormulaItem> = SyllabusCatalog.getFormulas()

    /**
     * Study Engine & Daily Target Logic:
     * 1. Automatic rollover at midnight (00:00): shifts overdue uncompleted targets into Backlog
     * 2. Automatically assigns 5 daily topics if fewer than 5 are assigned for today
     * 3. Streak verification: checks if 2 consecutive days were missed without freeze
     */
    suspend fun syncDailyEngineAndRollover(): RolloverSummary {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())

        // 0. Safety guarantee: ensure initial syllabus catalog is loaded if database is empty
        val currentTopicCount = topicDao.getTotalTopicsCount()
        if (currentTopicCount == 0) {
            chapterDao.insertAll(SyllabusCatalog.getChapters())
            topicDao.insertAll(SyllabusCatalog.getTopics(today))
            if (streakDao.getStreak() == null) {
                streakDao.insertOrUpdate(
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
            }
        }

        // 1. Shift uncompleted daily targets from earlier dates into Backlog
        val rolledOverCount = topicDao.markOverdueAsRollover(today)

        // 2. Ensure 5 daily topics are assigned for today
        val currentTodayList = topicDao.getTodayTargetsList(today)
        val needed = 5 - currentTodayList.size
        if (needed > 0) {
            val unassigned = topicDao.getUnassignedTopics(needed)
            if (unassigned.isNotEmpty()) {
                topicDao.assignTopicsDate(unassigned.map { it.id }, today)
            }
        }

        // 3. Check streak status on launch
        val streak = streakDao.getStreak() ?: StreakEntity(lastActiveDate = today)
        val lastActive = streak.lastActiveDate
        if (lastActive.isNotEmpty() && lastActive != today) {
            val daysDiff = calculateDaysDifference(lastActive, today)
            if (daysDiff >= 2) {
                // If 2 consecutive days missed without freeze shield protection, reset
                if (daysDiff == 2L && streak.freezeShieldAvailable && streak.freezesUsed < streak.totalFreezesAvailable) {
                    // Shield auto-preserves streak for 1 day grace period
                } else if (daysDiff > 2L || (!streak.freezeShieldAvailable)) {
                    // Reset streak if 2+ consecutive days missed
                    streakDao.update(streak.copy(currentStreak = 0, freezeShieldAvailable = true))
                }
            }
        }

        val backlogList = topicDao.getBacklogTopicsList()
        val catchUpMinutes = backlogList.sumOf { it.estimatedMinutes }
        return RolloverSummary(
            rolledOverCount = rolledOverCount,
            backlogTotal = backlogList.size,
            catchUpMinutes = catchUpMinutes
        )
    }

    suspend fun toggleTopicCompletion(topicId: Int, isCompleted: Boolean): StreakUpdateResult {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val timestamp = if (isCompleted) System.currentTimeMillis() else null

        topicDao.setTopicCompletion(topicId, isCompleted, timestamp)

        if (!isCompleted) {
            return StreakUpdateResult(isStreakUpdated = false, shieldUsed = false, newStreak = 0, xpGained = 0)
        }

        // Topic completed: earn +50 XP and record streak activity
        val streak = streakDao.getStreak() ?: StreakEntity(lastActiveDate = today)
        val lastActive = streak.lastActiveDate
        var newStreak = streak.currentStreak
        var shieldUsed = false
        var freezesUsed = streak.freezesUsed
        var shieldAvailable = streak.freezeShieldAvailable

        if (lastActive.isEmpty()) {
            newStreak = 1
        } else if (lastActive == today) {
            // Already logged today
        } else {
            val diff = calculateDaysDifference(lastActive, today)
            when {
                diff == 1L -> {
                    // Consecutive day
                    newStreak += 1
                }
                diff == 2L -> {
                    // 1-day grace period with active shield/freeze
                    if (shieldAvailable && freezesUsed < streak.totalFreezesAvailable) {
                        shieldUsed = true
                        freezesUsed += 1
                        shieldAvailable = false
                        newStreak += 1 // Shield preserves and continues streak
                    } else {
                        newStreak = 1 // Streak reset
                    }
                }
                else -> {
                    // 2 or more consecutive days missed: complete reset to 1
                    newStreak = 1
                }
            }
        }

        val bestStreak = max(streak.bestStreak, newStreak)
        val xpGained = 50
        val totalXp = streak.totalXp + xpGained
        val newLevel = (totalXp / 200) + 1
        val newHours = streak.estimatedMasteredHours + 0.4

        streakDao.insertOrUpdate(
            streak.copy(
                currentStreak = newStreak,
                lastActiveDate = today,
                freezesUsed = freezesUsed,
                freezeShieldAvailable = shieldAvailable,
                bestStreak = bestStreak,
                totalXp = totalXp,
                level = newLevel,
                estimatedMasteredHours = newHours
            )
        )

        return StreakUpdateResult(
            isStreakUpdated = true,
            shieldUsed = shieldUsed,
            newStreak = newStreak,
            xpGained = xpGained
        )
    }

    suspend fun moveBacklogTopicToToday(topicId: Int) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        topicDao.moveTopicToToday(topicId, today)
    }

    suspend fun recordStudySession(
        durationMinutes: Int,
        sessionType: String,
        topicName: String
    ): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = dateFormat.format(Date())
        val xpEarned = (durationMinutes / 5) * 10 // 50 XP for 25m

        sessionDao.insert(
            StudySessionEntity(
                date = today,
                durationMinutes = durationMinutes,
                sessionType = sessionType,
                topicName = topicName,
                xpEarned = xpEarned
            )
        )

        val streak = streakDao.getStreak() ?: StreakEntity(lastActiveDate = today)
        streakDao.insertOrUpdate(
            streak.copy(
                totalXp = streak.totalXp + xpEarned,
                estimatedMasteredHours = streak.estimatedMasteredHours + (durationMinutes / 60.0)
            )
        )
        return xpEarned
    }

    suspend fun buyFreezeShield(): Boolean {
        val streak = streakDao.getStreak() ?: return false
        if (streak.totalXp >= 200 && !streak.freezeShieldAvailable) {
            streakDao.insertOrUpdate(
                streak.copy(
                    totalXp = streak.totalXp - 200,
                    freezeShieldAvailable = true,
                    totalFreezesAvailable = streak.totalFreezesAvailable + 1
                )
            )
            return true
        }
        return false
    }

    suspend fun resetAllProgress(today: String) {
        topicDao.resetAllTopics()
        val unassigned = topicDao.getUnassignedTopics(5)
        if (unassigned.isNotEmpty()) {
            topicDao.assignTopicsDate(unassigned.map { it.id }, today)
        }
        sessionDao.clearAllSessions()
        streakDao.insertOrUpdate(
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
    }

    private fun calculateDaysDifference(fromDateStr: String, toDateStr: String): Long {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fromDate = format.parse(fromDateStr) ?: return 1L
            val toDate = format.parse(toDateStr) ?: return 1L
            val diffMs = toDate.time - fromDate.time
            TimeUnit.MILLISECONDS.toDays(diffMs)
        } catch (e: Exception) {
            1L
        }
    }
}

data class RolloverSummary(
    val rolledOverCount: Int,
    val backlogTotal: Int,
    val catchUpMinutes: Int
)

data class StreakUpdateResult(
    val isStreakUpdated: Boolean,
    val shieldUsed: Boolean,
    val newStreak: Int,
    val xpGained: Int
)
