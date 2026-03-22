package com.episeerr.app

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var errorLayout: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button

    private var isTvDevice = false
    private var serverUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Full-screen immersive mode
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        errorLayout = findViewById(R.id.errorLayout)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)

        isTvDevice = packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)

        val prefs = getSharedPreferences("episeerr", MODE_PRIVATE)
        serverUrl = prefs.getString("server_url", "http://192.168.254.205:5002") ?: "http://192.168.254.205:5002"

        // First launch: open settings if URL is default and user hasn't saved anything
        if (!prefs.contains("server_url")) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }

        setupWebView()
        loadEpiseerr()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.setSupportZoom(false)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.userAgentString = settings.userAgentString + if (isTvDevice) " EpiseerrTV/1.0 AndroidTV" else " EpiseerrApp/1.0"

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                if (request?.isForMainFrame == true) {
                    showError("Cannot connect to Episeerr server.\n\n${error?.description}\n\nCheck that the server is running and the URL is correct.")
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideError()
                if (isTvDevice) {
                    injectTvFocusScript()
                }
            }
        }
    }

    private fun loadEpiseerr() {
        val route = if (isTvDevice) "/tv" else "/"
        webView.loadUrl(serverUrl + route)
    }

    private fun injectTvFocusScript() {
        val script = """
            (function() {
                if (window._episeerrTvInit) return;
                window._episeerrTvInit = true;

                // Add TV focus styles
                var style = document.createElement('style');
                style.textContent = `
                    *:focus {
                        outline: 4px solid #007bff !important;
                        outline-offset: 4px !important;
                        box-shadow: 0 0 0 6px rgba(0,123,255,0.3) !important;
                    }
                `;
                document.head.appendChild(style);

                var focusableSelector = 'a, button, [tabindex]:not([tabindex="-1"]), input, select, .card, .stat-pill, .calendar-day-section';

                function getFocusables() {
                    return Array.from(document.querySelectorAll(focusableSelector))
                        .filter(el => el.offsetParent !== null && !el.disabled);
                }

                function getCurrentIndex(els) {
                    return els.indexOf(document.activeElement);
                }

                document.addEventListener('keydown', function(e) {
                    var els = getFocusables();
                    var idx = getCurrentIndex(els);
                    if (idx === -1 && els.length > 0) { els[0].focus(); return; }

                    switch(e.keyCode) {
                        case 38: // Up
                        case 37: // Left
                            e.preventDefault();
                            if (idx > 0) els[idx - 1].focus();
                            break;
                        case 40: // Down
                        case 39: // Right
                            e.preventDefault();
                            if (idx < els.length - 1) els[idx + 1].focus();
                            break;
                        case 13: // Enter/OK
                        case 23: // DPAD_CENTER
                            e.preventDefault();
                            if (document.activeElement) document.activeElement.click();
                            break;
                    }
                });

                // Auto-focus first element
                if (els.length > 0) els[0].focus();
            })();
        """.trimIndent()
        webView.evaluateJavascript(script, null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack()
                return true
            }
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            startActivity(Intent(this, SettingsActivity::class.java))
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        // Reload if URL changed in settings
        val prefs = getSharedPreferences("episeerr", MODE_PRIVATE)
        val currentUrl = prefs.getString("server_url", "http://192.168.254.205:5002") ?: ""
        if (currentUrl != serverUrl && currentUrl.isNotEmpty()) {
            serverUrl = currentUrl
            loadEpiseerr()
        }
    }

    private fun showError(message: String) {
        errorText.text = message
        errorLayout.visibility = View.VISIBLE
        webView.visibility = View.GONE
        retryButton.setOnClickListener {
            hideError()
            loadEpiseerr()
        }
        findViewById<Button>(R.id.settingsButton).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun hideError() {
        errorLayout.visibility = View.GONE
        webView.visibility = View.VISIBLE
    }
}
