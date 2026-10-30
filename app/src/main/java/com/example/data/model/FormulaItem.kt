package com.example.data.model

data class FormulaItem(
    val id: String,
    val title: String,
    val category: String, // "Algebra", "Calculus", "Probability", "Commercial Math", "Discrete Math", "Number Theory"
    val formulaDisplay: String,
    val latexCode: String,
    val explanation: String,
    val complexity: String = "O(1)",
    val isStarred: Boolean = false,
    val isHighYield: Boolean = false,
    val drillSteps: List<String> = emptyList(),
    val sampleProblem: String = "",
    val sampleSolution: String = ""
)
