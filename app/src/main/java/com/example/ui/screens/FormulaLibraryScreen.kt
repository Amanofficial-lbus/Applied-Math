package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FormulaItem
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
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
fun FormulaLibraryScreen(
    formulas: List<FormulaItem>,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSelectFormulaForSolver: (FormulaItem) -> Unit,
    onShowMessage: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val categories = listOf("All (45)", "Algebra", "Calculus", "Probability", "Commercial Math", "★ Starred")
    var expandedFolders by remember { mutableStateOf(setOf("Calculus & Limits", "Algebra")) }

    val filteredFormulas = remember(formulas, searchQuery, selectedCategory) {
        formulas.filter { formula ->
            val matchesQuery = searchQuery.isEmpty() ||
                    formula.title.contains(searchQuery, ignoreCase = true) ||
                    formula.formulaDisplay.contains(searchQuery, ignoreCase = true) ||
                    formula.explanation.contains(searchQuery, ignoreCase = true)
            val matchesCat = when (selectedCategory) {
                "All (45)", "All" -> true
                "★ Starred" -> formula.isStarred
                else -> formula.category.equals(selectedCategory, ignoreCase = true)
            }
            matchesQuery && matchesCat
        }
    }

    val featuredFormula = remember(formulas) {
        formulas.firstOrNull { it.id == "f_bayes" } ?: formulas.firstOrNull()
    }

    val formulasByFolder = remember(formulas) {
        mapOf(
            "Algebra" to formulas.filter { it.category == "Algebra" },
            "Calculus & Limits" to formulas.filter { it.category == "Calculus" },
            "Probability & Stats" to formulas.filter { it.category == "Probability" },
            "Commercial Math" to formulas.filter { it.category == "Commercial Math" }
        )
    }

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
                modifier = Modifier.testTag("formula_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Formula Library",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Quick-Reference • 45 Rules",
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

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("formula_search_field"),
            placeholder = { Text("Search formulas (e.g. Bayes, Limits)...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextSecondary
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateCard,
                unfocusedContainerColor = SlateCard,
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = SlateCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Chips Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { cat ->
                val isSelected = (cat == selectedCategory) || (cat == "All (45)" && selectedCategory == "All")
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) CyanAccent else SlateCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyanAccent else SlateCardBorder
                    ),
                    modifier = Modifier.clickable { onCategoryChange(if (cat.startsWith("All")) "All" else cat) }
                ) {
                    Text(
                        text = cat,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick Drill Card (Bayes' Theorem featured card as in page 6)
            if (searchQuery.isEmpty() && featuredFormula != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("featured_drill_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF132238)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFF0E7490).copy(alpha = 0.5f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent)
                                    ) {
                                        Text(
                                            text = "QUICK DRILL",
                                            color = CyanAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = featuredFormula.title,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Starred",
                                    tint = FlameYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Large formatted math equation box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F172A),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = featuredFormula.formulaDisplay,
                                        color = CyanAccent,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = featuredFormula.explanation,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Action buttons: Copy LaTeX | Explain | Solve Quiz
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        copyToClipboard(context, featuredFormula.latexCode)
                                        onShowMessage("Copied LaTeX to clipboard: ${featuredFormula.latexCode}")
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy LaTeX", fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = { onSelectFormulaForSolver(featuredFormula) },
                                    modifier = Modifier.weight(0.9f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder)
                                ) {
                                    Text("Explain", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { onSelectFormulaForSolver(featuredFormula) },
                                    modifier = Modifier.weight(1.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent)
                                ) {
                                    Text("Solve Quiz", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Category Folders Section Header
            item {
                Text(
                    text = "CATEGORY FOLDERS",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            // Category Folders
            val folderCategories = listOf(
                FolderCategory("Algebra", "12 formulas • Matrix, Roots, Logarithms", Color(0xFFF59E0B)),
                FolderCategory("Calculus & Limits", "18 formulas • Derivatives, Integrals, Series", Color(0xFF22C55E)),
                FolderCategory("Probability & Stats", "10 formulas • Bayes, Distributions, Z-Scores", Color(0xFF06B6D4)),
                FolderCategory("Commercial Math", "8 formulas • Annuity, EMI, Perpetuity, CAGR", Color(0xFFEC4899))
            )

            items(folderCategories) { folder ->
                val isExpanded = expandedFolders.contains(folder.title)
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
                                    expandedFolders = if (isExpanded) {
                                        expandedFolders - folder.title
                                    } else {
                                        expandedFolders + folder.title
                                    }
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(folder.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Folder,
                                        contentDescription = null,
                                        tint = folder.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = folder.title,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (folder.title.startsWith("Calculus")) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = StreakGreenMuted
                                            ) {
                                                Text(
                                                    text = "Active",
                                                    color = StreakGreenLight,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = folder.subtitle,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }

                        // Expanded list of formula cards
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val categoryFormulas = formulasByFolder[folder.title] ?: emptyList()

                                categoryFormulas.forEach { item ->
                                    FormulaCompactCard(
                                        formula = item,
                                        onSelect = { onSelectFormulaForSolver(item) },
                                        onCopy = {
                                            copyToClipboard(context, item.latexCode)
                                            onShowMessage("Copied: ${item.latexCode}")
                                        }
                                    )
                                }
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

@Composable
fun FormulaCompactCard(
    formula: FormulaItem,
    onSelect: () -> Unit,
    onCopy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formula.title,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (formula.isHighYield) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FlameOrange.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, FlameOrange)
                        ) {
                            Text(
                                text = "High-Yield",
                                color = FlameYellow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formula.formulaDisplay,
                    color = CyanAccent,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopy, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy LaTeX",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

data class FolderCategory(val title: String, val subtitle: String, val color: Color)

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Math Formula LaTeX", text)
    clipboard.setPrimaryClip(clip)
}
