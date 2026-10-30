package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.StepByStepSolverDialog
import com.example.ui.screens.AiMathTutorScreen
import com.example.ui.screens.BacklogScreen
import com.example.ui.screens.FormulaLibraryScreen
import com.example.ui.screens.HeatmapScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PomodoroScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SyllabusScreen
import com.example.ui.screens.ThreeJsMathLabScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.StreakGreen
import com.example.ui.theme.StreakGreenLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isBacklogDrawerOpen by viewModel.isBacklogDrawerOpen.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()

    val streak by viewModel.streak.collectAsStateWithLifecycle()
    val todayTargets by viewModel.todayTargets.collectAsStateWithLifecycle()
    val backlogTopics by viewModel.backlogTopics.collectAsStateWithLifecycle()
    val allTopics by viewModel.allTopics.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    val completedCount by viewModel.completedCount.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()

    val pomodoroMode by viewModel.pomodoroMode.collectAsStateWithLifecycle()
    val timerSecondsRemaining by viewModel.timerSecondsRemaining.collectAsStateWithLifecycle()
    val isTimerRunning by viewModel.isTimerRunning.collectAsStateWithLifecycle()
    val selectedTopicForSession by viewModel.selectedTopicForSession.collectAsStateWithLifecycle()
    val completedPomodorosToday by viewModel.completedPomodorosToday.collectAsStateWithLifecycle()
    val currentAmbientSound by viewModel.currentAmbientSound.collectAsStateWithLifecycle()
    val isAmbientSoundActive by viewModel.isAmbientSoundActive.collectAsStateWithLifecycle()

    val formulaSearchQuery by viewModel.formulaSearchQuery.collectAsStateWithLifecycle()
    val selectedFormulaCategory by viewModel.selectedFormulaCategory.collectAsStateWithLifecycle()
    val activeFormulaForSolver by viewModel.activeFormulaForSolver.collectAsStateWithLifecycle()
    val syllabusTabMode by viewModel.syllabusTabMode.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle Back Button: if on subscreen or drawer, go back to Home
    BackHandler(enabled = isBacklogDrawerOpen || currentTab != NavigationTab.HOME) {
        if (isBacklogDrawerOpen) {
            viewModel.setBacklogDrawerOpen(false)
        } else {
            viewModel.selectTab(NavigationTab.HOME)
        }
    }

    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground),
        containerColor = SlateBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = SlateCard,
                    contentColor = TextPrimary,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, StreakGreenLight.copy(alpha = 0.5f), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                ) {
                    Text(
                        text = data.visuals.message,
                        fontWeight = FontWeight.Bold,
                        color = StreakGreenLight,
                        fontSize = 13.sp
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SlateCard,
                contentColor = TextSecondary,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                    )
                    .testTag("bottom_nav_bar")
            ) {
                val navItems = listOf(
                    NavItem(NavigationTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
                    NavItem(NavigationTab.AI_TUTOR, "AI Tutor", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
                    NavItem(NavigationTab.LAB_3D, "3D Lab", Icons.Filled.ViewInAr, Icons.Outlined.ViewInAr),
                    NavItem(NavigationTab.FOCUS, "Focus", Icons.Filled.Timer, Icons.Outlined.Timer),
                    NavItem(NavigationTab.SYLLABUS, "Syllabus", Icons.Filled.Book, Icons.Outlined.Book),
                    NavItem(NavigationTab.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            if (item.tab == NavigationTab.SYLLABUS && backlogTopics.isNotEmpty()) {
                                BadgedBox(
                                    badge = {
                                        Badge(
                                            containerColor = FlameOrange,
                                            contentColor = Color.White
                                        ) {
                                            Text(
                                                text = "${backlogTopics.size}",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = StreakGreenLight,
                            indicatorColor = StreakGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            streak = streak,
                            todayTargets = todayTargets,
                            backlogTopics = backlogTopics,
                            completedCount = completedCount,
                            totalCount = totalCount,
                            onToggleTopic = { id, done -> viewModel.toggleTopicCompletion(id, done) },
                            onOpenSyllabus = { viewModel.openSyllabus(SyllabusTabMode.CURRICULUM) },
                            onOpenAnalytics = { viewModel.openAnalytics() },
                            onOpenBacklog = { viewModel.setBacklogDrawerOpen(true) },
                            onOpenPomodoro = { viewModel.selectTab(NavigationTab.FOCUS) },
                            onOpenFormulaLibrary = { viewModel.openSyllabus(SyllabusTabMode.FORMULAS) },
                            onOpenAiMentor = { viewModel.selectTab(NavigationTab.AI_TUTOR) },
                            onOpen3dLab = { viewModel.selectTab(NavigationTab.LAB_3D) },
                            onOpenProfile = { viewModel.selectTab(NavigationTab.PROFILE) }
                        )
                    }
                    NavigationTab.AI_TUTOR -> {
                        AiMathTutorScreen(
                            aiService = viewModel.aiService,
                            onBack = { viewModel.selectTab(NavigationTab.HOME) }
                        )
                    }
                    NavigationTab.LAB_3D -> {
                        ThreeJsMathLabScreen(
                            initialMode = "lpp",
                            onBack = { viewModel.selectTab(NavigationTab.HOME) }
                        )
                    }
                    NavigationTab.SYLLABUS -> {
                        // Provides seamless segmented toggle between Syllabus curriculum breakdown and Formula Library
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SlateBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (syllabusTabMode == SyllabusTabMode.CURRICULUM) CyanAccent else SlateCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (syllabusTabMode == SyllabusTabMode.CURRICULUM) CyanAccent else SlateCardBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSyllabusTabMode(SyllabusTabMode.CURRICULUM) }
                                ) {
                                    Text(
                                        text = "Curriculum (93 Topics)",
                                        color = if (syllabusTabMode == SyllabusTabMode.CURRICULUM) Color.Black else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (syllabusTabMode == SyllabusTabMode.FORMULAS) CyanAccent else SlateCard,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (syllabusTabMode == SyllabusTabMode.FORMULAS) CyanAccent else SlateCardBorder
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setSyllabusTabMode(SyllabusTabMode.FORMULAS) }
                                ) {
                                    Text(
                                        text = "Formula Library (45 Rules)",
                                        color = if (syllabusTabMode == SyllabusTabMode.FORMULAS) Color.Black else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }

                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                if (syllabusTabMode == SyllabusTabMode.CURRICULUM) {
                                    SyllabusScreen(
                                        chapters = chapters,
                                        allTopics = allTopics,
                                        completedCount = completedCount,
                                        totalCount = totalCount,
                                        onToggleTopic = { id, done -> viewModel.toggleTopicCompletion(id, done) },
                                        onBack = { viewModel.selectTab(NavigationTab.HOME) }
                                    )
                                } else {
                                    FormulaLibraryScreen(
                                        formulas = viewModel.allFormulas,
                                        searchQuery = formulaSearchQuery,
                                        selectedCategory = selectedFormulaCategory,
                                        onSearchChange = { viewModel.setFormulaSearchQuery(it) },
                                        onCategoryChange = { viewModel.setFormulaCategory(it) },
                                        onSelectFormulaForSolver = { viewModel.selectFormulaForSolver(it) },
                                        onShowMessage = { msg ->
                                            scope.launch { snackbarHostState.showSnackbar(msg) }
                                        },
                                        onBack = { viewModel.selectTab(NavigationTab.HOME) }
                                    )
                                }
                            }
                        }
                    }
                    NavigationTab.FOCUS -> {
                        PomodoroScreen(
                            mode = pomodoroMode,
                            secondsRemaining = timerSecondsRemaining,
                            isRunning = isTimerRunning,
                            selectedTopic = selectedTopicForSession,
                            completedPomodoros = completedPomodorosToday,
                            currentAmbientSound = currentAmbientSound,
                            isAmbientSoundActive = isAmbientSoundActive,
                            onModeChange = { viewModel.setPomodoroMode(it) },
                            onStart = { viewModel.startPomodoro() },
                            onPause = { viewModel.pausePomodoro() },
                            onReset = { viewModel.resetPomodoro() },
                            onSkip = { viewModel.skipPomodoro() },
                            onAmbientSoundChange = { viewModel.setAmbientSound(it) },
                            onToggleAmbientSound = { viewModel.toggleAmbientSoundPlayback() },
                            onBack = { viewModel.selectTab(NavigationTab.HOME) }
                        )
                    }
                    NavigationTab.HEATMAP -> {
                        HeatmapScreen(
                            streak = streak,
                            sessions = sessions,
                            onBack = { viewModel.selectTab(NavigationTab.HOME) }
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            streak = streak,
                            completedCount = completedCount,
                            totalCount = totalCount,
                            currentUser = currentUser,
                            isSyncing = isSyncing,
                            onSignInWithGoogle = { viewModel.signInWithGoogle() },
                            onSignOut = { viewModel.signOut() },
                            onBackupToCloud = { viewModel.backupToCloud() },
                            onRestoreFromCloud = { viewModel.restoreFromCloud() },
                            onBuyShield = { viewModel.buyFreezeShield() },
                            onResetProgress = { viewModel.resetAllProgress() },
                            onOpenAnalytics = { viewModel.openAnalytics() },
                            onBack = { viewModel.selectTab(NavigationTab.HOME) }
                        )
                    }
                }
            }
        }
    }

    // Backlog Modal Bottom Sheet / Drawer (Slides up with all rollover modules)
    if (isBacklogDrawerOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setBacklogDrawerOpen(false) },
            sheetState = sheetState,
            containerColor = SlateBackground,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .clip(CircleShape)
                        .background(SlateCardBorder)
                )
            }
        ) {
            BacklogScreen(
                backlogTopics = backlogTopics,
                onClose = { viewModel.setBacklogDrawerOpen(false) },
                onMoveToToday = { topicId ->
                    viewModel.moveBacklogTopicToToday(topicId)
                },
                onCompleteTopic = { topicId ->
                    viewModel.toggleTopicCompletion(topicId, true)
                },
                onStartFastRecovery = {
                    viewModel.fastRecoveryCramAction()
                }
            )
        }
    }

    // Interactive Step-by-Step Formula Solver Modal Dialog
    StepByStepSolverDialog(
        formula = activeFormulaForSolver,
        onDismiss = { viewModel.selectFormulaForSolver(null) }
    )
}

data class NavItem(
    val tab: NavigationTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
