package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AmbientSoundType
import com.example.ui.PomodoroMode
import com.example.ui.components.AmbientSoundDialog
import com.example.ui.components.CircularProgressIndicatorWithContent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.StreakGreen
import com.example.ui.theme.StreakGreenLight
import com.example.ui.theme.StreakGreenMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PomodoroScreen(
    mode: PomodoroMode,
    secondsRemaining: Int,
    isRunning: Boolean,
    selectedTopic: String,
    completedPomodoros: Int,
    currentAmbientSound: AmbientSoundType,
    isAmbientSoundActive: Boolean,
    onModeChange: (PomodoroMode) -> Unit,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit,
    onAmbientSoundChange: (AmbientSoundType) -> Unit,
    onToggleAmbientSound: () -> Unit,
    onBack: () -> Unit
) {
    var showSoundDialog by remember { mutableStateOf(false) }

    val totalSeconds = mode.minutes * 60
    val progress = (secondsRemaining.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)
    val minutes = secondsRemaining / 60
    val seconds = secondsRemaining % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("pomodoro_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pomodoro Focus",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Applied Mathematics",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Mode Switcher Pills: Focus 25m | Short 5m | Long 15m
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PomodoroMode.values().forEach { m ->
                    val isSelected = m == mode
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) Color(0xFF0E7490) else Color.Transparent,
                        modifier = Modifier
                            .testTag("pomodoro_mode_${m.name}")
                            .clickable { onModeChange(m) }
                    ) {
                        Text(
                            text = m.displayName,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Large Glowing Circular Countdown Timer
        CircularProgressIndicatorWithContent(
            progress = progress,
            modifier = Modifier.size(240.dp),
            strokeWidth = 14.dp,
            progressColors = listOf(CyanAccent, StreakGreen, StreakGreenLight),
            enableAnimation = false
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeFormatted,
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StreakGreenMuted.copy(alpha = 0.6f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isRunning) StreakGreen else TextMuted)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRunning) "In Focus Mode" else "Paused",
                            color = if (isRunning) StreakGreenLight else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Selected Task Pill
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session: $selectedTopic",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Controls: Reset / Skip / Play-Pause
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IconButton(
                onClick = onReset,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .testTag("pomodoro_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Big Glowing Play / Pause Button
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(if (isRunning) Color(0xFF0E7490) else StreakGreen)
                    .clickable {
                        if (isRunning) onPause() else onStart()
                    }
                    .testTag("pomodoro_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Play",
                    tint = if (isRunning) Color.White else Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = onSkip,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SlateCard)
                    .testTag("pomodoro_skip_button")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Skip",
                    tint = TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Today: 3 of 4 completed pills
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Today: $completedPomodoros of 4 completed",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..4) {
                    val isDone = i <= completedPomodoros
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isDone) CyanAccent else SlateCardBorder)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Ambient Sound Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 72.dp)
                .testTag("ambient_sound_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleAmbientSound,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isAmbientSoundActive) StreakGreenMuted else Color(0xFF0F172A))
                    ) {
                        Icon(
                            imageVector = if (isAmbientSoundActive) Icons.Default.Headphones else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Audio",
                            tint = if (isAmbientSoundActive) StreakGreenLight else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "Ambient Sound",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAmbientSoundActive) currentAmbientSound.displayName else "Muted / Off",
                            color = if (isAmbientSoundActive) CyanAccent else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { showSoundDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                ) {
                    Text(
                        text = "Change",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    AmbientSoundDialog(
        currentSound = currentAmbientSound,
        isOpen = showSoundDialog,
        onDismiss = { showSoundDialog = false },
        onSelectSound = {
            onAmbientSoundChange(it)
            showSoundDialog = false
        }
    )
}
