package com.example.cryptosignal.data.model

data class Candlestick(
    val close: Float,
    val high: Float,
    val low: Float,
    val open: Float,
    val open_time: String,
)