package com.itza2k.kore.ui.screens.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itza2k.kore.api.GeminiApiService
import com.itza2k.kore.viewmodel.KoreViewModel
import kotlinx.coroutines.launch

@Composable
fun AiScreen(viewModel: KoreViewModel) {
    val scrollState = rememberScrollState()
    var apiKey by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var isApiKeySet by remember { mutableStateOf(false) }
    // Using only Gemini as the LLM provider
    var aiResponse by remember { mutableStateOf("") }
    var userQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val geminiApiService = remember { GeminiApiService() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        AiHeader()

        Spacer(modifier = Modifier.height(24.dp))

        if (!isApiKeySet) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AI Assistant Setup",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "To use the AI assistant, you need to set up your API key.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Set API Key")
                    }
                }
            }
        } else {
            // Using Gemini as the LLM provider
            Spacer(modifier = Modifier.height(16.dp))

            // AI Chat Interface
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "AI Assistant",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (aiResponse.isEmpty() && !isLoading && errorMessage.isEmpty()) {
                            Text(
                                text = "Ask me anything about your habits, goals, or how to improve your productivity and sustainability practices.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else if (errorMessage.isNotEmpty()) {
                            Text(
                                text = "Error: $errorMessage",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Text(
                                text = aiResponse,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Column {
                        OutlinedTextField(
                            value = userQuery,
                            onValueChange = { userQuery = it },
                            label = { Text("Ask a question") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (userQuery.isNotEmpty() && apiKey.isNotEmpty()) {
                                    isLoading = true
                                    errorMessage = ""

                                    // Create a context-aware prompt that includes user data
                                    val habits = viewModel.habits.joinToString(", ") { it.name }
                                    val goals = viewModel.goals.joinToString(", ") { it.name }
                                    val contextPrompt = """
                                        As an AI assistant for a productivity and sustainability app, help the user with their query.

                                        User's habits: $habits
                                        User's goals: $goals
                                        Total points: ${viewModel.totalPoints}

                                        User query: $userQuery
                                    """.trimIndent()

                                    coroutineScope.launch {
                                        // Using Gemini API
                                        geminiApiService.generateContent(contextPrompt, apiKey)
                                            .onSuccess { response ->
                                                aiResponse = response
                                            }
                                            .onFailure { error ->
                                                errorMessage = error.message ?: "Unknown error occurred"
                                            }
                                        isLoading = false
                                    }
                                } else if (apiKey.isEmpty()) {
                                    errorMessage = "Please set your API key first"
                                }
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Send")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Reset API Key Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (errorMessage.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { errorMessage = "" }
                    ) {
                        Text("Clear Error")
                    }
                }

                OutlinedButton(
                    onClick = { 
                        isApiKeySet = false
                        apiKey = ""
                    }
                ) {
                    Text("Reset API Key")
                }
            }
        }
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Enter API Key") },
            text = {
                Column {
                    Text("Please enter your API key for the selected LLM provider.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        label = { Text("API Key") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "Hide" else "Show")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (apiKey.isNotEmpty()) {
                            isApiKeySet = true
                            showApiKeyDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showApiKeyDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AiHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI Assistant",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Get personalized insights and recommendations based on your habits and goals",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

// LlmSelectionChip removed as we're only using Gemini
