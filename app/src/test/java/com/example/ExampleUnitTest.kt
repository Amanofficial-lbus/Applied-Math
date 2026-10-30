package com.example

import com.example.data.SyllabusCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun syllabusContainsExact93TopicsAnd13Chapters() {
        val chapters = SyllabusCatalog.getChapters()
        assertEquals("There must be 13 syllabus chapters per syllabus specification", 13, chapters.size)

        val topics = SyllabusCatalog.getTopics("2026-10-29")
        assertEquals("Syllabus must contain all 93 syllabus topics", 93, topics.size)
        assertTrue("All 93 topics must start uncompleted in fresh zero state", topics.all { !it.isCompleted })

        val chapterTotalSum = chapters.sumOf { it.totalTopics }
        assertEquals("Sum of topics in chapters must match 93", 93, chapterTotalSum)

        // Verify chapter topic distributions match the syllabus table
        val expectedPerChapter = listOf(5, 5, 13, 8, 4, 9, 10, 5, 7, 12, 5, 6, 4)
        chapters.forEachIndexed { index, chapter ->
            val count = topics.count { it.chapterId == chapter.id }
            assertEquals(
                "Chapter ${chapter.id} (${chapter.name}) must have ${expectedPerChapter[index]} topics",
                expectedPerChapter[index],
                count
            )
        }
    }

    @Test
    fun formulaLibraryContainsComprehensiveRules() {
        val formulas = SyllabusCatalog.getFormulas()
        assertTrue("Formula library must contain key formulas", formulas.isNotEmpty())
        assertTrue("Contains Bayes Theorem", formulas.any { it.id == "f_bayes" })
        assertTrue("Contains Modulo Exponentiation", formulas.any { it.id == "f_mod_exp" })
        assertTrue("Contains Taylor Series", formulas.any { it.id == "f_taylor" })
        assertTrue("Contains Integration by Parts", formulas.any { it.id == "f_int_parts" })
        assertTrue("Contains EMI formula", formulas.any { it.id == "f_emi" })
    }
}
