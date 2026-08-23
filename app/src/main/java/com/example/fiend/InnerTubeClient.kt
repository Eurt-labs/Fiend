package com.example.fiend

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class InnerTubeClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor(AdblockInterceptor()) // Add our custom Rust Adblock Interceptor
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    // A minimal context payload required by the InnerTube API
    private fun getContextPayload(): JSONObject {
        val context = JSONObject()
        val clientInfo = JSONObject().apply {
            put("clientName", "WEB_REMIX")
            put("clientVersion", "1.20240101.01.00")
        }
        context.put("client", clientInfo)
        return JSONObject().put("context", context)
    }

    /**
     * Fetches recommended songs for the home screen from YT Music.
     */
    fun fetchRecommendations(): List<MusicItem> {
        val payload = getContextPayload().toString()
        
        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/browse")
            .post(payload.toRequestBody(jsonMediaType))
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // In a real implementation, you would parse the complex InnerTube JSON response here.
                // For demonstration, returning a mocked list representing the extracted data.
                listOf(
                    MusicItem("dQw4w9WgXcQ", "Never Gonna Give You Up", "Rick Astley", "https://i.ytimg.com/vi/dQw4w9WgXcQ/hqdefault.jpg"),
                    MusicItem("kJQP7kiw5Fk", "Despacito", "Luis Fonsi", "https://i.ytimg.com/vi/kJQP7kiw5Fk/hqdefault.jpg")
                )
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Extracts the actual streaming URL (e.g. m4a/opus) for a given video ID to bypass ads.
     */
    fun getStreamUrl(videoId: String): String? {
        val payload = getContextPayload().apply {
            put("videoId", videoId)
        }.toString()

        val request = Request.Builder()
            .url("https://music.youtube.com/youtubei/v1/player")
            .post(payload.toRequestBody(jsonMediaType))
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                // Real implementation parses the streamingData.adaptiveFormats array 
                // and selects the best ITag (like 140 for m4a).
                // Mocking the stream URL for demo purposes.
                "https://example.com/mock_stream.m4a"
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
