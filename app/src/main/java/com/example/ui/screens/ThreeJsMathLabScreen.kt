package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardBorder
import com.example.ui.theme.StreakGreen
import com.example.ui.theme.StreakGreenLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ThreeJsMathLabScreen(
    initialMode: String = "lpp", // "lpp", "matrix", "calc"
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var selectedMode by remember { mutableStateOf(initialMode) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            webViewRef?.let { wv ->
                wv.evaluateJavascript("window.pauseLab();", null)
                wv.onPause()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("threejs_lab_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ViewInAr,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "3D Math Lab",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { webViewRef?.reload() },
                modifier = Modifier.testTag("threejs_reload_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload 3D Scene",
                    tint = TextSecondary
                )
            }
        }

        // Quick Mode Selector Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val modes = listOf(
                Triple("lpp", "13.1 LPP 3D Polyhedron", "Feasible Region"),
                Triple("matrix", "3.1 Matrix Planes", "Eigenvectors"),
                Triple("calc", "6.2 Tangent Surface", "Derivatives")
            )

            modes.forEach { (modeKey, title, _) ->
                val isSelected = selectedMode == modeKey
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) CyanAccent.copy(alpha = 0.2f) else SlateCard,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) CyanAccent else SlateCardBorder
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedMode = modeKey
                            webViewRef?.evaluateJavascript("window.setMathMode('$modeKey');", null)
                        }
                ) {
                    Text(
                        text = if (modeKey == "lpp") "LPP 3D" else if (modeKey == "matrix") "Matrices" else "Calculus",
                        color = if (isSelected) CyanAccent else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Full-bleed Three.js WebGL Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
                        isNestedScrollingEnabled = false
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            useWideViewPort = false
                            loadWithOverviewMode = true
                        }
                        setBackgroundColor(0xFF0F172A.toInt())
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                view?.evaluateJavascript("window.setMathMode('$selectedMode');", null)
                            }
                        }
                        loadUrl("file:///android_asset/threejs/math_3d_lab.html")
                        webViewRef = this
                    }
                },
                update = { webView ->
                    webViewRef = webView
                },
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("threejs_webgl_canvas")
            )
        }
    }
}
