package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundManager
import com.example.audio.AmbientSoundType
import com.example.data.RolloverSummary
import com.example.data.StudyRepository
import com.example.data.db.AppDatabase
import com.example.data.model.ChapterEntity
import com.example.data.model.FormulaItem
import com.example.data.model.StreakEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TopicEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.example.data.ai.AiStudyMentorService
import com.example.data.cloud.CloudBackupPayload
import com.example.data.cloud.CloudSyncManager
import com.google.firebase.auth.FirebaseUser

enum class PomodoroMode(val displayName: String, val minutes: Int) {
    FOCUS("Focus 25m", 25),
    SHORT_BREAK("Short 5m", 5),
    LONG_BREAK("Long 15m", 15)
}

enum class SyllabusTabMode(val title: String) {
    CURRICULUM("Curriculum (93 Topics)"),
    FORMULAS("Formula Library (45 Rules)")
}

enum class NavigationTab(val label: String) {
    HOME("Home"),
    SYLLABUS("Syllabus"),
    FOCUS("Focus"),
    AI_TUTOR("AI Mentor"),
    LAB_3D("3D Lab"),
    HEATMAP("Analytics"),
    PROFILE("Profile")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = StudyRepository(db)
    val ambientSoundManager = AmbientSoundManager()
    val aiService = AiStudyMentorService()
    val cloudSyncManager = CloudSyncManager(application)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDate: String = dateFormat.format(Date())

    // Repository Flows
    val streak: StateFlow<StreakEntity?> = repository.streakFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayTargets: StateFlow<List<TopicEntity>> = repository.getTodayTargets(todayDate)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val backlogTopics: StateFlow<List<TopicEntity>> = repository.getBacklogTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTopics: StateFlow<List<TopicEntity>> = repository.allTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<ChapterEntity>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedCount: StateFlow<Int> = repository.completedTopicsCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCount: StateFlow<Int> = repository.totalTopicsCountFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 93)

    val sessions: StateFlow<List<StudySessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Backlog state
    private val _currentTab = MutableStateFlow(NavigationTab.HOME)
    val currentTab: StateFlow<NavigationTab> = _currentTab.asStateFlow()

    private val _isBacklogDrawerOpen = MutableStateFlow(false)
    val isBacklogDrawerOpen: StateFlow<Boolean> = _isBacklogDrawerOpen.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    // Formula Library State
    private val _formulaSearchQuery = MutableStateFlow("")
    val formulaSearchQuery: StateFlow<String> = _formulaSearchQuery.asStateFlow()

    private val _selectedFormulaCategory = MutableStateFlow("All")
    val selectedFormulaCategory: StateFlow<String> = _selectedFormulaCategory.asStateFlow()

    private val _activeFormulaForSolver = MutableStateFlow<FormulaItem?>(null)
    val activeFormulaForSolver: StateFlow<FormulaItem?> = _activeFormulaForSolver.asStateFlow()

    val allFormulas: List<FormulaItem> = repository.getFormulas()

    // Pomodoro Timer State
    private val _pomodoroMode = MutableStateFlow(PomodoroMode.FOCUS)
    val pomodoroMode: StateFlow<PomodoroMode> = _pomodoroMode.asStateFlow()

    private val _timerSecondsRemaining = MutableStateFlow(25 * 60)
    val timerSecondsRemaining: StateFlow<Int> = _timerSecondsRemaining.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedTopicForSession = MutableStateFlow("1.1 Modulo Arithmetic • Numbers")
    val selectedTopicForSession: StateFlow<String> = _selectedTopicForSession.asStateFlow()

    private val _completedPomodorosToday = MutableStateFlow(0)
    val completedPomodorosToday: StateFlow<Int> = _completedPomodorosToday.asStateFlow()

    private val _currentAmbientSound = MutableStateFlow(AmbientSoundType.BINAURAL_ALPHA)
    val currentAmbientSound: StateFlow<AmbientSoundType> = _currentAmbientSound.asStateFlow()

    private val _isAmbientSoundActive = MutableStateFlow(false)
    val isAmbientSoundActive: StateFlow<Boolean> = _isAmbientSoundActive.asStateFlow()

    private var timerJob: Job? = null

    init {
        viewModelScope.launch {
            // Run automatic daily engine & rollover at midnight / app launch
            val summary: RolloverSummary = repository.syncDailyEngineAndRollover()
            if (summary.rolledOverCount > 0) {
                _notificationMessage.value = "${summary.rolledOverCount} overdue targets transferred to Backlog"
            }
        }
    }

    private val _syllabusTabMode = MutableStateFlow(SyllabusTabMode.CURRICULUM)
    val syllabusTabMode: StateFlow<SyllabusTabMode> = _syllabusTabMode.asStateFlow()

    fun selectTab(tab: NavigationTab) {
        _currentTab.value = tab
    }

    fun setSyllabusTabMode(mode: SyllabusTabMode) {
        _syllabusTabMode.value = mode
    }

    fun openSyllabus(mode: SyllabusTabMode = SyllabusTabMode.CURRICULUM) {
        _syllabusTabMode.value = mode
        _currentTab.value = NavigationTab.SYLLABUS
    }

    fun openAnalytics() {
        _currentTab.value = NavigationTab.HEATMAP
    }

    fun setBacklogDrawerOpen(open: Boolean) {
        _isBacklogDrawerOpen.value = open
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }

    fun toggleTopicCompletion(topicId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleTopicCompletion(topicId, isCompleted)
            if (isCompleted) {
                if (result.shieldUsed) {
                    _notificationMessage.value = "🛡️ Streak Shield Used! 1-day grace saved your streak (${result.newStreak} days) +${result.xpGained} XP"
                } else {
                    _notificationMessage.value = "🎯 Topic Mastered! +${result.xpGained} XP | Streak: ${result.newStreak} 🔥"
                }
            }
        }
    }

    fun moveBacklogTopicToToday(topicId: Int) {
        viewModelScope.launch {
            repository.moveBacklogTopicToToday(topicId)
            _notificationMessage.value = "Moved topic to Today's Target"
        }
    }

    fun fastRecoveryCramAction() {
        viewModelScope.launch {
            val earnedXp = repository.recordStudySession(45, "Overdue Cram", "Cram: Overdue Module Catch-Up")
            _notificationMessage.value = "⚡ Rapid 45m Recovery Drill completed! +${earnedXp + 15} XP"
            _isBacklogDrawerOpen.value = false
        }
    }

    // Pomodoro Controls
    fun setPomodoroMode(mode: PomodoroMode) {
        pausePomodoro()
        _pomodoroMode.value = mode
        _timerSecondsRemaining.value = mode.minutes * 60
    }

    fun startPomodoro() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true

        if (_isAmbientSoundActive.value && !ambientSoundManager.isPlaying) {
            ambientSoundManager.play()
        }

        timerJob = viewModelScope.launch {
            while (_timerSecondsRemaining.value > 0 && _isTimerRunning.value) {
                delay(1000L)
                _timerSecondsRemaining.value -= 1
            }
            if (_timerSecondsRemaining.value <= 0) {
                onPomodoroCompleted()
            }
        }
    }

    fun pausePomodoro() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        if (ambientSoundManager.isPlaying) {
            ambientSoundManager.stop()
        }
    }

    fun resetPomodoro() {
        pausePomodoro()
        _timerSecondsRemaining.value = _pomodoroMode.value.minutes * 60
    }

    fun skipPomodoro() {
        pausePomodoro()
        val nextMode = when (_pomodoroMode.value) {
            PomodoroMode.FOCUS -> PomodoroMode.SHORT_BREAK
            PomodoroMode.SHORT_BREAK -> PomodoroMode.FOCUS
            PomodoroMode.LONG_BREAK -> PomodoroMode.FOCUS
        }
        setPomodoroMode(nextMode)
    }

    private fun onPomodoroCompleted() {
        _isTimerRunning.value = false
        if (ambientSoundManager.isPlaying) {
            ambientSoundManager.stop()
        }
        val mode = _pomodoroMode.value
        val minutes = mode.minutes
        viewModelScope.launch {
            val xpEarned = repository.recordStudySession(
                durationMinutes = minutes,
                sessionType = mode.displayName,
                topicName = _selectedTopicForSession.value
            )
            _completedPomodorosToday.value += 1
            _notificationMessage.value = "🎉 ${mode.displayName} complete! +$xpEarned XP earned"
            // Switch mode
            if (mode == PomodoroMode.FOCUS) {
                val next = if (_completedPomodorosToday.value % 4 == 0) PomodoroMode.LONG_BREAK else PomodoroMode.SHORT_BREAK
                setPomodoroMode(next)
            } else {
                setPomodoroMode(PomodoroMode.FOCUS)
            }
        }
    }

    fun setAmbientSound(type: AmbientSoundType) {
        _currentAmbientSound.value = type
        ambientSoundManager.setSoundType(type)
        if (type == AmbientSoundType.OFF) {
            _isAmbientSoundActive.value = false
            ambientSoundManager.stop()
        } else {
            _isAmbientSoundActive.value = true
            if (_isTimerRunning.value) {
                ambientSoundManager.play()
            }
        }
    }

    fun toggleAmbientSoundPlayback() {
        val newState = !_isAmbientSoundActive.value
        _isAmbientSoundActive.value = newState
        if (newState) {
            if (_currentAmbientSound.value == AmbientSoundType.OFF) {
                _currentAmbientSound.value = AmbientSoundType.BINAURAL_ALPHA
                ambientSoundManager.setSoundType(AmbientSoundType.BINAURAL_ALPHA)
            }
            ambientSoundManager.play()
        } else {
            ambientSoundManager.stop()
        }
    }

    fun setSelectedTopicForSession(topicName: String) {
        _selectedTopicForSession.value = topicName
    }

    fun buyFreezeShield() {
        viewModelScope.launch {
            val success = repository.buyFreezeShield()
            if (success) {
                _notificationMessage.value = "🛡️ Streak Freeze Shield Equipped! Streak protected."
            } else {
                _notificationMessage.value = "Shield already active or insufficient XP."
            }
        }
    }

    fun setFormulaSearchQuery(query: String) {
        _formulaSearchQuery.value = query
    }

    fun setFormulaCategory(category: String) {
        _selectedFormulaCategory.value = category
    }

    fun selectFormulaForSolver(formula: FormulaItem?) {
        _activeFormulaForSolver.value = formula
    }

    // Firebase Auth and Firestore Cloud Sync
    val currentUser: StateFlow<FirebaseUser?> = cloudSyncManager.authStateFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), cloudSyncManager.currentUser)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun signInWithGoogle() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = cloudSyncManager.signInWithGoogle()
            if (res.isSuccess) {
                _notificationMessage.value = "Signed in as ${res.getOrNull()?.email ?: "User"}!"
                backupToCloud()
            } else {
                _notificationMessage.value = "Sign-in cancelled or failed."
            }
            _isSyncing.value = false
        }
    }

    fun signOut() {
        cloudSyncManager.signOut()
        _notificationMessage.value = "Signed out of Google account."
    }

    fun backupToCloud() {
        val user = cloudSyncManager.currentUser ?: run {
            _notificationMessage.value = "Please sign in with Google to back up."
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            val currentStreakEntity = streak.value
            val completedIds = allTopics.value.filter { it.isCompleted }.map { it.id }
            val rolloverIds = backlogTopics.value.map { it.id }

            val payload = CloudBackupPayload(
                userId = user.uid,
                currentStreak = currentStreakEntity?.currentStreak ?: 0,
                totalXp = currentStreakEntity?.totalXp ?: 0,
                completedTopicIds = completedIds,
                rolloverTopicIds = rolloverIds,
                freezesUsed = currentStreakEntity?.freezesUsed ?: 0,
                bestStreak = currentStreakEntity?.bestStreak ?: 0,
                level = currentStreakEntity?.level ?: 1,
                rankTitle = currentStreakEntity?.rankTitle ?: "Rank #1 Contender",
                lastActiveDate = currentStreakEntity?.lastActiveDate ?: todayDate,
                freezeShieldAvailable = currentStreakEntity?.freezeShieldAvailable ?: true,
                estimatedMasteredHours = currentStreakEntity?.estimatedMasteredHours ?: 0.0
            )

            val res = cloudSyncManager.uploadBackup(payload)
            if (res.isSuccess) {
                _notificationMessage.value = "Syllabus progress backed up to Firestore!"
            } else {
                _notificationMessage.value = "Backup failed: ${res.exceptionOrNull()?.localizedMessage}"
            }
            _isSyncing.value = false
        }
    }

    fun restoreFromCloud() {
        val user = cloudSyncManager.currentUser ?: run {
            _notificationMessage.value = "Please sign in with Google to restore."
            return
        }
        viewModelScope.launch {
            _isSyncing.value = true
            val res = cloudSyncManager.fetchLatestBackup()
            if (res.isSuccess) {
                val backup = res.getOrNull()
                if (backup != null) {
                    backup.completedTopicIds.forEach { topicId ->
                        repository.toggleTopicCompletion(topicId, true)
                    }
                    _notificationMessage.value = "Restored ${backup.completedTopicIds.size} completed topics from Firestore!"
                } else {
                    _notificationMessage.value = "No previous cloud backup found."
                }
            } else {
                _notificationMessage.value = "Restore failed: ${res.exceptionOrNull()?.localizedMessage}"
            }
            _isSyncing.value = false
        }
    }

    fun resetAllProgress() {
        pausePomodoro()
        resetPomodoro()
        _completedPomodorosToday.value = 0
        viewModelScope.launch {
            repository.resetAllProgress(todayDate)
            _notificationMessage.value = "All syllabus progress, streaks, and statistics have been reset to 0."
        }
    }

    override fun onCleared() {
        super.onCleared()
        ambientSoundManager.stop()
    }
}
