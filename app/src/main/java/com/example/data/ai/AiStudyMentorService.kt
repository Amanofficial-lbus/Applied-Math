@file:OptIn(com.google.firebase.ai.type.PublicPreviewAPI::class)

package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

private const val TAG = "AiStudyMentorService"

enum class MessageSender {
    USER, AI, SYSTEM
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val modelUsed: String = "gemini-3.8-flash",
    val isSearchGrounded: Boolean = false
)

enum class AiTaskComplexity {
    FAST_LITE,   // gemini-3.1-flash-lite: Rapid speed checks, quick formulas
    GENERAL,     // gemini-3.8-flash: Multi-turn reasoning, concept guidance
    COMPLEX_MATH // gemini-4-argon: Complex proofs, full step derivations, LPP optimization
}

class AiStudyMentorService {

    // System instruction for the 100/100 CBSE Class 12 Applied Mathematics Mentor & Vedic Speed Coach
    private val mathMentorSystemInstruction = content {
        text(
            """
            You are the Master CBSE Class 12 Applied Mathematics (Code 241) Mentor & Exam Strategist.
            Your ultimate mission is to empower students to achieve a perfect 100/100 score in their Class 12 Board Exams.
            
            Key Pedagogy & Rules:
            1. Concept Clarity First: Provide rigorous, intuitive, and crystal-clear explanations of syllabus topics (Numbers & Quantification, Numerical Inequalities, Matrices & Determinants, Calculus & Derivatives, Differential Equations, Probability & Inferential Statistics, Time Based Data, Financial Mathematics, and Linear Programming).
            2. Step-by-Step Board Exam Solutions: Always break down calculations into numbered steps matching CBSE marking schemes.
            3. Vedic Speed Shortcuts: Whenever applicable, provide fast mental calculation tricks:
               - Urdhva Tiryagbhyam (Vertically and Crosswise) for rapid 2x2 and 3x3 matrix multiplication and determinants.
               - Nikhilam Navatashcaramam Dashatah for modulo arithmetic, large powers, and percentage calculations in financial math.
               - Ekadhikena Purvena for quick squaring and compound growth rates.
            4. Tone: Encouraging, rigorous, disciplined, and laser-focused on mathematical mastery.
            5. Math Notation: Format mathematical expressions clearly using readable standard mathematical notation.
            """.trimIndent()
        )
    }

    // Lazy model initializations per Firebase AI Logic contract
    val generalModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = mathMentorSystemInstruction
        )
    }

    val searchGroundedModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            tools = listOf(Tool.googleSearch()),
            systemInstruction = mathMentorSystemInstruction
        )
    }

    val complexArgonModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-4-argon",
            systemInstruction = mathMentorSystemInstruction
        )
    }

    val fastLiteModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.1-flash-lite",
            systemInstruction = mathMentorSystemInstruction
        )
    }

    val liveVoiceModel by lazy {
        Firebase.ai.liveModel(
            modelName = "gemini-3.8-live",
            systemInstruction = mathMentorSystemInstruction
        )
    }

    /**
     * Streams multi-turn chat response with model selection based on complexity
     * and optional Google Search Grounding.
     */
    fun streamChatResponse(
        history: List<ChatMessage>,
        userMessage: String,
        complexity: AiTaskComplexity = AiTaskComplexity.GENERAL,
        enableSearchGrounding: Boolean = false
    ): Flow<String> = flow {
        val model = when {
            enableSearchGrounding -> searchGroundedModel
            complexity == AiTaskComplexity.COMPLEX_MATH -> complexArgonModel
            complexity == AiTaskComplexity.FAST_LITE -> fastLiteModel
            else -> generalModel
        }

        // Build history contents
        val historyContents = history.takeLast(10).map { msg ->
            content(role = if (msg.sender == MessageSender.USER) "user" else "model") {
                text(msg.text)
            }
        }

        val chat = model.startChat(history = historyContents)

        try {
            withTimeout(60_000L) {
                chat.sendMessageStream(userMessage).collect { chunk ->
                    val chunkText = chunk.text.orEmpty()
                    if (chunkText.isNotEmpty()) {
                        emit(chunkText)
                    }
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Chat response timed out after 60s", e)
            emit("\n[Request timed out. Please check your network and try again.]")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Chat generation error", e)
            emit("\n[AI Mentor error: ${e.localizedMessage ?: "Unable to complete response."}]")
        }
    }

    /**
     * Unary response for fast problem solving or voice transcript processing.
     */
    suspend fun solveMathPrompt(
        prompt: String,
        complexity: AiTaskComplexity = AiTaskComplexity.GENERAL,
        useSearch: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val model = when {
            useSearch -> searchGroundedModel
            complexity == AiTaskComplexity.COMPLEX_MATH -> complexArgonModel
            complexity == AiTaskComplexity.FAST_LITE -> fastLiteModel
            else -> generalModel
        }

        try {
            withTimeout(60_000L) {
                val response = model.generateContent(prompt)
                response.text ?: "No solution generated."
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Generation timed out", e)
            "Solving timed out. Please try a simpler breakdown or retry."
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Generation failed", e)
            "AI Tutor Error: ${e.localizedMessage ?: "Failed to generate solution."}"
        }
    }
}
