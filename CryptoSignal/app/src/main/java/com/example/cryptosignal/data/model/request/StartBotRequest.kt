package com.example.cryptosignal.data.model.request

data class StartBotRequest(
    val symbol: String,
    val interval: String,
    val indicators: Map<String, Any>
)