package com.example.wikiapp.viewModel

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WikipediaDetailScreen(title: String) {
    var canGoBack by remember { mutableStateOf(false) }
    val webViewState = remember { WebViewState() }
    val history = remember { mutableStateListOf("https://en.wikipedia.org/wiki/$title") }
    val viewModel: WikipediaViewModel = hiltViewModel()
    LaunchedEffect(title) {
        viewModel.addToHistory(title)
    }

    BackHandler(enabled = canGoBack) {
        if (webViewState.webView?.canGoBack() == true) {
            webViewState.webView?.goBack()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(1f)
        ) {

        }


        Box {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                if (url != null && url != history.lastOrNull()) {
                                    history.add(url)
                                }
                                canGoBack = view?.canGoBack() == true
                            }
                        }
                        webChromeClient = WebChromeClient()
                        settings.javaScriptEnabled = true
                        loadUrl("https://en.wikipedia.org/wiki/$title")
                        webViewState.webView = this
                    }
                },
                update = { webView ->
                    canGoBack = webView.canGoBack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

class WebViewState {
    var webView: WebView? = null
}