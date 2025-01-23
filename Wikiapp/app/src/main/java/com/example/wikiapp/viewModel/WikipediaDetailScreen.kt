package com.example.wikiapp.viewModel

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

@SuppressLint("SetJavaScriptEnabled", "CoroutineCreationDuringComposition")
@Composable
fun WikipediaDetailScreen(title: String, viewModel: WikipediaViewModel) {
    var canGoBack by remember { mutableStateOf(false) }
    var currentUrl by remember { mutableStateOf("") }
    var htmlContent by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = {
                if (htmlContent.isNotEmpty()) {
                    viewModel.savePageForOffline(
                        title = title,
                        url = currentUrl,
                        htmlContent = htmlContent
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = !isLoading && htmlContent.isNotEmpty()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Save for Offline")
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = null
                                }
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                coroutineScope.launch {
                                    isLoading = false
                                    url?.let {
                                        currentUrl = it
                                        view?.evaluateJavascript(
                                            """
                                            (function() {
                                                try {
                                                    return document.documentElement.outerHTML;
                                                } catch(e) {
                                                    return "<html><body>Error loading content</body></html>";
                                                }
                                            })();
                                            """.trimIndent()
                                        ) { html ->
                                            htmlContent = html
                                                ?.replace("\\\"", "\"")
                                                ?.replace("\\n", "\n")
                                                ?.replace("\\'", "'")
                                                ?: "<html><body>Empty content</body></html>"
                                        }
                                    }
                                    canGoBack = view?.canGoBack() == true
                                }
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                errorCode: Int,
                                description: String?,
                                failingUrl: String?
                            ) {
                                coroutineScope.launch {
                                    isLoading = false
                                    errorMessage = "Page load error: ${description ?: "Unknown error"}"
                                }
                            }
                        }
                        webChromeClient = WebChromeClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowContentAccess = true
                        }
                        loadUrl("https://en.wikipedia.org/wiki/$title")
                    }
                },
                update = { webView ->
                    canGoBack = webView.canGoBack()
                }
            )

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
