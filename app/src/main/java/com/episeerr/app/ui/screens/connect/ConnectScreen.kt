package com.episeerr.app.ui.screens.connect

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ConnectScreen(
    viewModel: ConnectViewModel = hiltViewModel(),
    onConnected: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.connected, uiState.needsLogin) {
        if (uiState.connected && !uiState.needsLogin) {
            onConnected()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Episeerr", style = MaterialTheme.typography.headlineMedium)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

        if (!uiState.needsLogin) {
            Text("Connect to your Episeerr server", style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = uiState.serverUrl,
                onValueChange = viewModel::onServerUrlChange,
                label = { Text("Server URL") },
                placeholder = { Text("http://192.168.1.10:5002") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = viewModel::connect, enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text("Connect")
                }
            }
        } else {
            Text("This server requires a login", style = MaterialTheme.typography.bodyMedium)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(4.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(Icons.Filled.Visibility, contentDescription = "Toggle password visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = viewModel::login, enabled = !uiState.isLoading, modifier = Modifier.fillMaxWidth()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text("Log in")
                }
            }
        }

        uiState.error?.let { error ->
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }
}
