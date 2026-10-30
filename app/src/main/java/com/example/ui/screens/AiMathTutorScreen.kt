package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiStudyMentorService
import com.example.data.ai.AiTaskComplexity
import com.example.data.ai.ChatMessage
import com.example.data.ai.MessageSender
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameYellow
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.StreakGreen
import com.example.ui.theme.StreakGreenLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AiMathTutorScreen(
    aiService: AiStudyMentorService,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var isVoiceMode by remember { mutableStateOf(false) }
    var selectedComplexity by remember { mutableStateOf(AiTaskComplexity.GENERAL) }
    var enableSearchGrounding by remember { mutableStateOf(false) }

    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    // Pre-populate with welcome message from the 100/100 mentor
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = MessageSender.AI,
                    text = "Welcome to your 100/100 CBSE Class 12 Applied Mathematics Coaching Room!\n\nI am your Exam Strategist and Vedic Speed Tutor. Ask me any syllabus derivation, step-by-step problem (Matrices, Calculus, LPP, Inferential Stats, Financial Math), or request Vedic speed calculation shortcuts.",
                    modelUsed = "gemini-3.8-flash"
                )
            )
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Smooth auto-scroll when new messages arrive without thrashing animations during streaming
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val onSendMessage: (String) -> Unit = { textToSend ->
        if (textToSend.isNotBlank() && !isGenerating) {
            val userMsg = ChatMessage(
                sender = MessageSender.USER,
                text = textToSend.trim()
            )
            val placeholderAiMsg = ChatMessage(
                sender = MessageSender.AI,
                text = "",
                isStreaming = true,
                modelUsed = when {
                    enableSearchGrounding -> "gemini-3.8-flash (Search)"
                    selectedComplexity == AiTaskComplexity.COMPLEX_MATH -> "gemini-4-argon"
                    selectedComplexity == AiTaskComplexity.FAST_LITE -> "gemini-3.1-flash-lite"
                    else -> "gemini-3.8-flash"
                },
                isSearchGrounded = enableSearchGrounding
            )

            messages = messages + userMsg + placeholderAiMsg
            inputText = ""
            isGenerating = true

            coroutineScope.launch {
                val fullResponse = StringBuilder()
                try {
                    aiService.streamChatResponse(
                        history = messages.dropLast(1),
                        userMessage = userMsg.text,
                        complexity = selectedComplexity,
                        enableSearchGrounding = enableSearchGrounding
                    ).collect { chunk ->
                        fullResponse.append(chunk)
                        val updatedList = messages.toMutableList()
                        if (updatedList.isNotEmpty() && updatedList.last().sender == MessageSender.AI) {
                            updatedList[updatedList.lastIndex] = updatedList.last().copy(
                                text = fullResponse.toString(),
                                isStreaming = true
                            )
                            messages = updatedList
                        }
                    }
                } finally {
                    val updatedList = messages.toMutableList()
                    if (updatedList.isNotEmpty() && updatedList.last().sender == MessageSender.AI) {
                        updatedList[updatedList.lastIndex] = updatedList.last().copy(
                            isStreaming = false
                        )
                        messages = updatedList
                    }
                    isGenerating = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("ai_tutor_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isVoiceMode) "Live Voice Coach" else "Gemini Math Mentor",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "100/100 Target • CBSE Code 241",
                    color = StreakGreenLight,
                    fontSize = 11.sp
                )
            }

            // Toggle between Chat and Voice Mode
            IconButton(
                onClick = { isVoiceMode = !isVoiceMode },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isVoiceMode) FlameOrange.copy(alpha = 0.2f) else SlateCard)
                    .testTag("toggle_voice_mode_button")
            ) {
                Icon(
                    imageVector = if (isVoiceMode) Icons.Default.School else Icons.Default.Mic,
                    contentDescription = if (isVoiceMode) "Switch to Chat" else "Switch to Voice",
                    tint = if (isVoiceMode) FlameOrange else CyanAccent
                )
            }
        }

        // Secondary Toolbar: Model Selector & Search Grounding Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model Complexity Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedComplexity == AiTaskComplexity.GENERAL) CyanAccent.copy(alpha = 0.2f) else SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedComplexity == AiTaskComplexity.GENERAL) CyanAccent else SlateCardBorder),
                    modifier = Modifier.clickable { selectedComplexity = AiTaskComplexity.GENERAL }
                ) {
                    Text(
                        text = "3.8 Flash",
                        color = if (selectedComplexity == AiTaskComplexity.GENERAL) CyanAccent else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedComplexity == AiTaskComplexity.COMPLEX_MATH) FlameOrange.copy(alpha = 0.2f) else SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedComplexity == AiTaskComplexity.COMPLEX_MATH) FlameOrange else SlateCardBorder),
                    modifier = Modifier.clickable { selectedComplexity = AiTaskComplexity.COMPLEX_MATH }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "4 Argon",
                            color = if (selectedComplexity == AiTaskComplexity.COMPLEX_MATH) FlameOrange else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedComplexity == AiTaskComplexity.FAST_LITE) StreakGreen.copy(alpha = 0.2f) else SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (selectedComplexity == AiTaskComplexity.FAST_LITE) StreakGreen else SlateCardBorder),
                    modifier = Modifier.clickable { selectedComplexity = AiTaskComplexity.FAST_LITE }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = StreakGreen, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Fast Lite",
                            color = if (selectedComplexity == AiTaskComplexity.FAST_LITE) StreakGreen else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Google Search Grounding Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (enableSearchGrounding) Color(0xFF1D4ED8).copy(alpha = 0.25f) else SlateCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (enableSearchGrounding) Color(0xFF60A5FA) else SlateCardBorder),
                modifier = Modifier
                    .clickable { enableSearchGrounding = !enableSearchGrounding }
                    .testTag("search_grounding_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = if (enableSearchGrounding) Color(0xFF93C5FD) else TextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (enableSearchGrounding) "Search ON" else "Search Grounding",
                        color = if (enableSearchGrounding) Color(0xFF93C5FD) else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isVoiceMode) {
            // Live Voice Coach View
            VoiceCoachLiveView(
                aiService = aiService,
                onTranscribedQuestion = { spokenText ->
                    isVoiceMode = false
                    onSendMessage(spokenText)
                }
            )
        } else {
            // Multi-Turn Chat View
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 120.dp)
                ) {
                    items(messages, key = { it.id }) { msg ->
                        ChatMessageBubble(message = msg)
                    }

                    if (isGenerating && messages.lastOrNull()?.text.isNullOrEmpty()) {
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = CyanAccent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Analyzing with ${if (enableSearchGrounding) "Google Search Grounding" else selectedComplexity.name}...",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Bottom Floating Input Panel
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SlateBackground.copy(alpha = 0.95f), SlateBackground)
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Quick Suggested Prompts
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        val prompts = listOf(
                            "Vedic trick for 3x3 Determinant",
                            "Explain Leontief Input-Output model",
                            "CBSE 2026 Sample Paper Pattern",
                            "Solve: 13^200 mod 7 step-by-step",
                            "EMI vs Sinking Fund derivations"
                        )
                        items(prompts) { prompt ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SlateCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                                modifier = Modifier.clickable { onSendMessage(prompt) }
                            ) {
                                Text(
                                    text = prompt,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    // Input Field with Send Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Ask math question or formula...", color = TextMuted, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ai_math_input_field"),
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SlateCard,
                                unfocusedContainerColor = SlateCard,
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = SlateCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { onSendMessage(inputText) })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { onSendMessage(inputText) },
                            enabled = inputText.isNotBlank() && !isGenerating,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) CyanAccent else SlateCard)
                                .testTag("ai_send_message_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color(0xFF0F172A) else TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Model & Search Grounding Label Badge for AI
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF1E293B)
                ) {
                    Text(
                        text = message.modelUsed,
                        color = CyanAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                if (message.isSearchGrounded) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1D4ED8).copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "Google Search Grounded",
                            color = Color(0xFF93C5FD),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Bubble Content
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Color(0xFF1E3A8A) else SlateCard,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) Color(0xFF3B82F6) else SlateCardBorder
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
fun VoiceCoachLiveView(
    aiService: AiStudyMentorService,
    onTranscribedQuestion: (String) -> Unit
) {
    var isListening by remember { mutableStateOf(false) }
    var voiceTranscript by remember { mutableStateOf("Tap the microphone below and speak your math question or request mental math practice...") }
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val waveScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Live Audio Math Coach",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Powered by gemini-3.8-live API",
                color = CyanAccent,
                fontSize = 12.sp
            )
        }

        // Animated Audio Waveform Ring
        Box(
            modifier = Modifier
                .size(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = (size.minDimension / 2.5f) * if (isListening) waveScale else 1f
                drawCircle(
                    color = if (isListening) CyanAccent.copy(alpha = 0.15f) else Color(0xFF1E293B),
                    radius = radius
                )
                drawCircle(
                    color = if (isListening) StreakGreen.copy(alpha = 0.25f) else Color(0xFF132238),
                    radius = radius * 0.8f
                )
            }

            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(if (isListening) StreakGreen else Color(0xFF1E293B))
                    .clickable { isListening = !isListening },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Microphone",
                    tint = if (isListening) Color(0xFF0F172A) else TextSecondary,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        // Live Transcript Display
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SlateCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isListening) "LISTENING TO VOICE..." else "VOICE SESSION READY",
                    color = if (isListening) StreakGreenLight else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = voiceTranscript,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // Quick Spoken Prompts Simulator (for interactive testing)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tap to simulate spoken questions:",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val q = "What is the Vedic trick for 2x2 matrix multiplication?"
                            voiceTranscript = "Spoken: \"$q\""
                            onTranscribedQuestion(q)
                        }
                ) {
                    Text(
                        text = "Vedic Matrix Trick",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SlateCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateCardBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            val q = "Explain the corner point method for LPP 3D"
                            voiceTranscript = "Spoken: \"$q\""
                            onTranscribedQuestion(q)
                        }
                ) {
                    Text(
                        text = "LPP Corner Points",
                        color = StreakGreenLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
