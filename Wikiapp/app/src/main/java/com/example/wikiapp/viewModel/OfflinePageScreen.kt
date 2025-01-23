package com.example.wikiapp.viewModel

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun OfflinePageScreen(
    pageId: Long,
    viewModel: WikipediaViewModel,
    onPageDeleted: () -> Unit
) {
    val page by viewModel.getPageById(pageId).collectAsState(initial = null)

    // Debug: Log raw HTML content
    LaunchedEffect(page) {
        page?.let {
            Log.d("OfflinePage", "Raw HTML Content (first 200 chars): ${it.htmlContent.take(200)}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Delete Button
        Button(
            onClick = {
                viewModel.deletePageById(pageId) // Delete the page from ViewModel
                onPageDeleted() // Callback to navigate or update UI
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Delete Page")
        }

        // WebView to display the page content
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }

                    // Load the cleaned HTML content
                    page?.htmlContent?.let { rawHtml ->
                        val decodedHtml = decodeHtmlEntities(rawHtml)
                            .replace(Regex("[\\x00-\\x1F\\x7F]"), "") // Remove control characters
                            .replace(Regex("\\s+"), " ") // Normalize whitespace
                            .replace(Regex(">\\s+<"), "><") // Remove spaces between tags
                            .trim()

                        Log.d("OfflinePage", "Cleaned HTML Content (first 200 chars): ${decodedHtml.take(200)}")

                        loadDataWithBaseURL(
                            "https://en.wikipedia.org",
                            decodedHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    } ?: loadUrl("about:blank")
                }
            },
            modifier = Modifier.weight(1f), // Fill remaining space
            update = { webView ->
                page?.htmlContent?.let { rawHtml ->
                    val decodedHtml = decodeHtmlEntities(rawHtml)
                        .replace(Regex("[\\x00-\\x1F\\x7F]"), "")
                        .replace(Regex("\\s+"), " ")
                        .replace(Regex(">\\s+<"), "><")
                        .trim()

                    Log.d("OfflinePage", "Updating WebView with Cleaned HTML Content (first 200 chars): ${decodedHtml.take(200)}")

                    webView.loadDataWithBaseURL(
                        "https://en.wikipedia.org",
                        decodedHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            }
        )
    }
}

// Function to decode HTML escape sequences
fun decodeHtmlEntities(input: String): String {
    return input
        .replace("\\u003C", "<")
        .replace("\\u003E", ">")
        .replace("\\u0022", "\"")
        .replace("\\u0027", "'")
        .replace(Regex("\\\\u([0-9A-Fa-f]{4})")) { match ->
            val hexCode = match.groups[1]?.value ?: return@replace match.value
            val codePoint = hexCode.toIntOrNull(16)
            if (codePoint != null) {
                String(Character.toChars(codePoint))
            } else {
                match.value
            }
        }
}
