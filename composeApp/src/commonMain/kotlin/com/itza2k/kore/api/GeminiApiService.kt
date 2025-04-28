package com.itza2k.kore.api

import com.itza2k.kore.api.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Service for interacting with the Gemini API
 */
class GeminiApiService(private val httpClient: HttpClient) {

    companion object {
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private const val GEMINI_MODEL = "gemini-2.0-flash"
    }

    /**
     * Creates a new HttpClient with JSON content negotiation
     */
    constructor() : this(HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    })

    /**
     * Generates content using the Gemini API
     *
     * @param prompt The text prompt to send to the API
     * @param apiKey The API key for authentication
     * @return The response from the Gemini API
     */
    suspend fun generateContent(prompt: String, apiKey: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        parts = listOf(
                            Part(text = prompt)
                        )
                    )
                )
            )

            val response = httpClient.post {
                url("$BASE_URL/$GEMINI_MODEL:generateContent")
                parameter("key", apiKey)
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status.isSuccess()) {
                val geminiResponse: GeminiResponse = response.body()
                val textResponse = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                
                if (textResponse != null) {
                    Result.success(textResponse)
                } else {
                    Result.failure(Exception("No text response found in the API response"))
                }
            } else {
                Result.failure(Exception("API request failed with status: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}