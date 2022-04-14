package com.example.elder.domain

import io.ktor.client.*
import io.ktor.client.request.*

class NetworkClient {
    private val client = HttpClient()
    suspend fun getJson(url: String): String = client.get(url)
}