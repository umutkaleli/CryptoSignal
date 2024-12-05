package com.example.cryptosignal.data.model.response

import com.example.cryptosignal.data.model.CoinDetails

data class WalletResponse(
    val cash_balance: Double,
    val details: List<CoinDetails>,
    val total_balance: Double,
    val total_wallet_value: Double
)
