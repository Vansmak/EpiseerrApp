package com.episeerr.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val urlInput = findViewById<EditText>(R.id.serverUrlInput)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        val prefs = getSharedPreferences("episeerr", MODE_PRIVATE)
        val savedUrl = prefs.getString("server_url", "http://192.168.254.205:5002") ?: ""
        urlInput.setText(savedUrl)

        saveButton.setOnClickListener {
            val url = urlInput.text.toString().trim().trimEnd('/')
            if (url.isEmpty()) {
                Toast.makeText(this, "Please enter a server URL", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(this, "URL must start with http:// or https://", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString("server_url", url).apply()
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }
}
