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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChapterEntity
import com.example.data.model.TopicEntity
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FlameOrange
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
fun SyllabusScreen(
    chapters: List<ChapterEntity>,
    allTopics: List<TopicEntity>,
    completedCount: Int,
    totalCount: Int,
    onToggleTopic: (Int, Boolean) -> Unit,
    onBack: () -> Unit
) {
    val total = remember(totalCount) { if (totalCount > 0) totalCount else 93 }
    val overallPercent = remember(completedCount, total) {
        if (total > 0) ((completedCount.toFloat() / total.toFloat()) * 100).toInt() else 64
    }
    val topicsByChapter = remember(allTopics) {
        allTopics.groupBy { it.chapterId }
    }
    var expandedChapterId by remember { mutableStateOf<Int?>(3) } // Calculus expanded by default

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp)
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
                modifier = Modifier.testTag("syllabus_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Your Syllabus Goal",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "High-level progress & curriculum setup",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E293B),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "Active Term",
                    color = CyanAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Overall Syllabus Cleared Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StreakGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Overall Syllabus Cleared",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "$overallPercent% ($completedCount/$total topics)",
                                color = StreakGreenLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { (completedCount.toFloat() / total.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = StreakGreen,
                            trackColor = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Milestones Labels: Foundation -> Midterm (50%) -> Current -> Mastery
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Foundation", color = TextMuted, fontSize = 10.sp)
                            Text("Midterm (50%)", color = TextMuted, fontSize = 10.sp)
                            Text("Current ($overallPercent%)", color = CyanAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Mastery (100%)", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Enrolled Subjects Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENROLLED SUBJECTS (${chapters.size})",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "93 Syllabus Topics",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // List of Subject Cards with progress and expandable topics
            items(chapters, key = { it.id }) { chapter ->
                val chapterTopics = topicsByChapter[chapter.id] ?: emptyList()
                val doneInChapter = chapterTopics.count { it.isCompleted }
                val totalInChapter = if (chapterTopics.isNotEmpty()) chapterTopics.size else chapter.totalTopics
                val percent = if (totalInChapter > 0) ((doneInChapter.toFloat() / totalInChapter.toFloat()) * 100).toInt() else 0
                val isExpanded = expandedChapterId == chapter.id
                val nextTopic = chapterTopics.firstOrNull { !it.isCompleted }?.name ?: "All Mastered!"

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedChapterId = if (isExpanded) null else chapter.id
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                val chapColor = parseChapterColor(chapter.colorHex)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(chapColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = getChapterIcon(chapter.iconName),
                                        contentDescription = null,
                                        tint = chapColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = chapter.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$doneInChapter of $totalInChapter topics complete",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "$percent%",
                                    color = if (percent >= 60) StreakGreenLight else CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Progress Bar inside card
                        LinearProgressIndicator(
                            progress = { (doneInChapter.toFloat() / totalInChapter.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(2.5.dp)),
                            color = parseChapterColor(chapter.colorHex),
                            trackColor = Color(0xFF0F172A)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Next: $nextTopic",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Expandable list of all topics in this chapter
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                chapterTopics.forEach { topic ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (topic.isCompleted) Color(0xFF132238) else Color(0xFF0F172A))
                                            .clickable { onToggleTopic(topic.id, !topic.isCompleted) }
                                            .padding(horizontal = 10.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (topic.isCompleted) CyanAccent else Color.Transparent)
                                                    .border(
                                                        1.dp,
                                                        if (topic.isCompleted) CyanAccent else SlateCardBorder,
                                                        RoundedCornerShape(6.dp)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (topic.isCompleted) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "${topic.code} ${topic.name}",
                                                color = if (topic.isCompleted) TextSecondary else TextPrimary,
                                                fontSize = 12.sp,
                                                textDecoration = if (topic.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                        }

                                        Text(
                                            text = "${topic.estimatedMinutes}m",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

private fun getChapterIcon(name: String): ImageVector {
    return when (name) {
        "numbers" -> Icons.Default.Numbers
        "grid_view" -> Icons.Default.GridView
        "functions" -> Icons.Default.Functions
        "casino" -> Icons.Default.Casino
        "show_chart" -> Icons.Default.ShowChart
        "account_balance" -> Icons.Default.AccountBalance
        else -> Icons.Default.AltRoute
    }
}

private fun parseChapterColor(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (_: Exception) {
        CyanAccent
    }
}

