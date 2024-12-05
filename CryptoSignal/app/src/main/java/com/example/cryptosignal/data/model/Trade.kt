package com.example.cryptosignal.data.model

data class Trade(
    val symbol: String,
    val results: String,
    val balance: Double,
    val coinBalance: Double,
    val currentPrice: Double,
    val timestamp: String,
)
