package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StreakEntity
import com.example.data.model.TopicEntity
import com.example.ui.NavigationTab
import com.example.ui.components.CircularProgressIndicatorWithContent
import com.example.ui.components.StreakFlameBadge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FreezeShieldBlue
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.StreakGreen
import com.example.ui.theme.StreakGreenLight
import com.example.ui.theme.StreakGreenMuted
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    streak: StreakEntity?,
    todayTargets: List<TopicEntity>,
    backlogTopics: List<TopicEntity>,
    completedCount: Int,
    totalCount: Int,
    onToggleTopic: (Int, Boolean) -> Unit,
    onOpenSyllabus: () -> Unit,
    onOpenAnalytics: () -> Unit,
    onOpenBacklog: () -> Unit,
    onOpenPomodoro: () -> Unit,
    onOpenFormulaLibrary: () -> Unit,
    onOpenAiMentor: () -> Unit,
    onOpen3dLab: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val total = remember(totalCount) { if (totalCount > 0) totalCount else 93 }
    val progressPercent = remember(completedCount, total) {
        if (total > 0) ((completedCount.toFloat() / total.toFloat()) * 100).toInt() else 0
    }
    val todayCompleted = remember(todayTargets) { todayTargets.count { it.isCompleted } }
    val todayTotal = remember(todayTargets) { if (todayTargets.isNotEmpty()) todayTargets.size else 5 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Top Bar with Chips and Streak Flame
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "APPLIED MATHEMATICS",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StreakGreenMuted.copy(alpha = 0.5f),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { onOpenAnalytics() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StreakGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "On Track • Analytics",
                                    color = StreakGreenLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        StreakFlameBadge(
                            streakDays = streak?.currentStreak ?: 0,
                            shieldActive = streak?.freezeShieldAvailable ?: true,
                            onClick = onOpenProfile
                        )
                    }
                }
            }

            // Title Header
            item {
                Column(
                    modifier = Modifier.clickable { onOpenSyllabus() }
                ) {
                    Text(
                        text = "Whole Syllabus",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Class XII Core & Engineering Entrance • Tap to View",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            // Big Syllabus Completion Card with Glowing Circular Arc
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("syllabus_progress_card")
                        .clickable { onOpenSyllabus() },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Circular Progress indicator
                            CircularProgressIndicatorWithContent(
                                progress = completedCount.toFloat() / total.toFloat(),
                                modifier = Modifier.size(110.dp),
                                strokeWidth = 10.dp
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$progressPercent%",
                                        color = TextPrimary,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "FINISHED",
                                        color = CyanAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Progress stats beside circle
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "COMPLETED TOPICS",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "$completedCount ",
                                            color = TextPrimary,
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "/ $total",
                                            color = TextSecondary,
                                            fontSize = 14.sp
                                        )
                                    }
                                }

                                Column(modifier = Modifier.clickable { onOpenAnalytics() }) {
                                    Text(
                                        text = "ESTIMATED MASTERED",
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "${streak?.estimatedMasteredHours ?: 0.0} hrs",
                                        color = StreakGreenLight,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        // Clickable action strip
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Open Full 13 Chapters & 93 Topics",
                                color = CyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "View All →",
                                color = StreakGreenLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // AI Mentor & 3D Lab Quick Launch Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Gemini AI Math Mentor Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_ai_mentor_card")
                            .clickable { onOpenAiMentor() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CyanAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = "100/100 Coach",
                                        color = CyanAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Gemini Math AI",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Vedic tricks, step derivations & Voice Live tutor",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    // Three.js 3D Math Lab Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("home_3d_lab_card")
                            .clickable { onOpen3dLab() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132D24)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StreakGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(StreakGreen.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = StreakGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF1E293B)
                                ) {
                                    Text(
                                        text = "Three.js 60fps",
                                        color = StreakGreenLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "3D Math Lab",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Feasible polyhedrons, Matrix planes & Calculus",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // Today's Target Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Target (5 topics)",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0E7490).copy(alpha = 0.4f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "$todayCompleted / $todayTotal Completed",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 5 Daily Target Cards
            items(todayTargets, key = { it.id }) { topic ->
                DailyTargetCard(
                    topic = topic,
                    onToggle = { isChecked ->
                        onToggleTopic(topic.id, isChecked)
                    }
                )
            }

            // Bottom Quick Action Pills (Streak Freeze, Backlog, Formula Library)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Streak Freeze Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FreezeShieldBlue.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenProfile() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = "Freeze",
                                tint = FreezeShieldBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Streak Freeze (${streak?.totalFreezesAvailable ?: 3})",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Backlog Pill
                    val backlogCount = backlogTopics.size
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (backlogCount > 0) Color(0xFF3F1B22) else Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (backlogCount > 0) Color(0xFFF43F5E) else SlateCardBorder
                        ),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("backlog_pill_button")
                            .clickable { onOpenBacklog() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Backlog",
                                tint = if (backlogCount > 0) FlameOrange else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Backlog ($backlogCount Rollover)",
                                color = if (backlogCount > 0) FlameOrange else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Formula Library Pill
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E293B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onOpenFormulaLibrary() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Functions,
                                contentDescription = "Formulas",
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Formula Lib",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Big Launch Pomodoro Button
            item {
                Button(
                    onClick = onOpenPomodoro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("launch_pomodoro_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StreakGreen)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "POMODORO 25:00",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
fun DailyTargetCard(
    topic: TopicEntity,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("topic_target_${topic.id}")
            .clickable { onToggle(!topic.isCompleted) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (topic.isCompleted) Color(0xFF162338) else SlateCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (topic.isCompleted) CyanAccent.copy(alpha = 0.4f) else SlateCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (topic.isCompleted) CyanAccent else Color.Transparent)
                    .border(
                        1.5.dp,
                        if (topic.isCompleted) CyanAccent else SlateCardBorder,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (topic.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Topic name & notes
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${topic.code} ${topic.name}",
                    color = if (topic.isCompleted) TextSecondary else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (topic.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(2.dp))
                val subtitle = if (topic.notes.isNotEmpty()) "${topic.chapterName} • ${topic.notes}" else topic.chapterName
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Status Badge: "Done" or "25 min"
            if (topic.isCompleted) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0E7490).copy(alpha = 0.3f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "Done",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyanAccent)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${topic.estimatedMinutes} min",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
