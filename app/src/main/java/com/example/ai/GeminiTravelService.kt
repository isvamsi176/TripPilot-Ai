package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "GeminiTravelService"

data class ParsedTripPrompt(
    val origin: String,
    val destination: String,
    val days: Int,
    val travellers: Int,
    val budgetInr: Int,
    val preferences: List<String>,
    val aiSummary: String
)

sealed interface AiServiceResult<out T> {
    data class Success<T>(val data: T) : AiServiceResult<T>
    data class Error(val message: String) : AiServiceResult<Nothing>
}

class GeminiTravelService {

    // General chat and travel query model
    private val generalModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    // Structured JSON schema for trip prompt parser
    private val tripParserSchema = Schema.obj(
        properties = mapOf(
            "origin" to Schema.string(description = "Departure Indian or international city"),
            "destination" to Schema.string(description = "Target travel destination"),
            "days" to Schema.integer(description = "Duration in days"),
            "travellers" to Schema.integer(description = "Number of passengers/guests"),
            "budgetInr" to Schema.integer(description = "Target total budget in Indian Rupees (INR)"),
            "preferences" to Schema.array(
                items = Schema.string(),
                description = "Extracted vibes and interests e.g. Beaches, Heritage, Food"
            ),
            "aiSummary" to Schema.string(description = "Brief 1-sentence executive summary of the plan")
        )
    )

    private val structuredParserModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = tripParserSchema
            }
        )
    }

    suspend fun parseNaturalLanguageTrip(prompt: String): AiServiceResult<ParsedTripPrompt> {
        return try {
            val response = withTimeout(60_000L) {
                structuredParserModel.generateContent(
                    """
                    You are the Travel Planner Agent of TripPilot AI.
                    Parse the following user travel request into structured trip parameters with default currency INR (₹).
                    If a field is unspecified, make smart defaults for India travel (e.g. 5 days, 2 travellers, ₹25000 budget).
                    User prompt: "$prompt"
                    """.trimIndent()
                )
            }

            val rawJson = response.text ?: throw IllegalStateException("Model returned empty text")
            val json = JSONObject(rawJson)

            val origin = json.optString("origin", "Hyderabad")
            val destination = json.optString("destination", "Goa")
            val days = json.optInt("days", 5).coerceIn(1, 30)
            val travellers = json.optInt("travellers", 2).coerceIn(1, 20)
            val budgetInr = json.optInt("budgetInr", 25000).coerceAtLeast(5000)

            val prefArray = json.optJSONArray("preferences")
            val preferences = if (prefArray != null) {
                (0 until prefArray.length()).map { prefArray.getString(it) }
            } else {
                listOf("Beaches", "Sightseeing")
            }
            val summary = json.optString("aiSummary", "Custom planned itinerary powered by Gemini 3.8 Flash.")

            AiServiceResult.Success(
                ParsedTripPrompt(
                    origin = origin,
                    destination = destination,
                    days = days,
                    travellers = travellers,
                    budgetInr = budgetInr,
                    preferences = preferences,
                    aiSummary = summary
                )
            )
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Gemini parser timed out", e)
            AiServiceResult.Error("Gemini analysis timed out after 60s. Please retry.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Gemini parsing failed", e)
            AiServiceResult.Error(e.localizedMessage ?: "Gemini parsing failed.")
        }
    }

    suspend fun askTravelAssistant(question: String): AiServiceResult<String> {
        return try {
            val response = withTimeout(60_000L) {
                generalModel.generateContent(
                    """
                    You are the AI Travel Support Agent for TripPilot AI (India's premier multi-agent travel platform).
                    Answer concisely, professionally, and accurately with costs in Indian Rupees (₹ / INR).
                    Cover Indian Railways (IRCTC), domestic airlines, and local safety rules where relevant.
                    Question: $question
                    """.trimIndent()
                )
            }
            val text = response.text
            if (!text.isNullOrBlank()) {
                AiServiceResult.Success(text)
            } else {
                AiServiceResult.Error("Model returned an empty response.")
            }
        } catch (e: TimeoutCancellationException) {
            AiServiceResult.Error("Travel assistant request timed out.")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Assistant call failed", e)
            AiServiceResult.Error(e.localizedMessage ?: "Gemini generation failed.")
        }
    }
}
