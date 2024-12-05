package com.example.cryptosignal.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptosignal.data.api.RetrofitClient
import com.example.cryptosignal.data.model.Candlestick
import com.example.cryptosignal.data.model.CoinDetails
import com.example.cryptosignal.data.model.request.StartBotRequest
import com.example.cryptosignal.data.model.request.StopBotRequest
import com.example.cryptosignal.data.model.Trade
import com.example.cryptosignal.data.model.response.WalletResponse
import com.example.cryptosignal.util.Constants
import io.socket.client.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response

class MainViewModel : ViewModel() {

    private var socket = IO.socket(Constants.BASE_URL)
    var isBotStarted = mutableStateOf(false)
    val walletData = mutableStateOf<WalletResponse?>(null)
    val errorMessage = mutableStateOf<String?>(null)
    val selectedCoin = mutableStateOf(CoinDetails(0.0, 0.0, "BTCUSDT", 0.0))
    private val _tradeHistory = MutableStateFlow<List<Trade>>(emptyList())
    val tradeHistory: StateFlow<List<Trade>> = _tradeHistory


    val candlestickData = mutableStateOf<List<Candlestick>>(emptyList())

    // İndikatör değerleri (default 0)
    val rsiLValue = mutableStateOf(0)             // RSI (Relative Strength Index Lower Band)
    val rsiHValue = mutableStateOf(0)             // RSI (Relative Strength Index Upper Band)
    val smaValue = mutableStateOf(0)              // SMA (Simple Moving Average)
    val emaValue = mutableStateOf(0)              // EMA (Exponential Moving Average)
    val macdValue = mutableStateOf(0)             // MACD (Moving Average Convergence Divergence)
    val superTrendValue = mutableStateOf(0)       // SuperTrend
    val dmiValue = mutableStateOf(0)              // DMI (Directional Movement Index)
    val cciValue = mutableStateOf(0)              // CCI (Commodity Channel Index)
    val vwapValue = mutableStateOf(0)             // VWAP (Volume Weighted Average Price)
    val macdSignalValue = mutableStateOf(0)       // MACD Signal Line
    val bollingerLbandValue = mutableStateOf(0)   // Bollinger Lower Band
    val bollingerHbandValue = mutableStateOf(0)   // Bollinger Upper Band
    val ema20Value = mutableStateOf(0)            // EMA 20 (20-period Exponential Moving Average)


    val coinIndicators = mapOf(
        "BTCUSDT" to listOf("BOLLINGER_LBAND", "CCI", "RSI_L", "RSI_H", "BOLLINGER_HBAND"),
        "ETHUSDT" to listOf("BOLLINGER_LBAND", "VWAP", "RSI_L", "RSI_H", "BOLLINGER_HBAND"),
        "AVAXUSDT" to listOf("BOLLINGER_LBAND", "CCI", "RSI_L", "RSI_H", "BOLLINGER_HBAND"),
        "SOLUSDT" to listOf("EMA_20", "MACD", "RSI_L", "RSI_H", "BOLLINGER_HBAND"),
        "RENDERUSDT" to listOf("EMA_20", "MACD", "RSI_L", "RSI_H", "BOLLINGER_HBAND"),
        "FETUSDT" to listOf("BOLLINGER_LBAND", "VWAP", "RSI_L", "RSI_H", "BOLLINGER_HBAND")
    )

    init {
        fetchPrice("BTCUSDT")
        initializeSocket()
    }

    private fun initializeSocket() {
        try {
            setupSocketListeners()
            socket.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupSocketListeners() {
        socket.on("trade_update") { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                val resultsArray = data.getJSONArray("results")
                val results = mutableListOf<String>()
                for (i in 0 until resultsArray.length()) {
                    results.add(resultsArray.getString(i))
                }
                val trade = Trade(
                    symbol = data.getString("symbol"),
                    results = results.joinToString(", "), // Listeyi String olarak birleştir
                    balance = data.getDouble("balance"),
                    coinBalance = data.getDouble("coin_balance"),
                    currentPrice = data.getDouble("current_price"),
                    timestamp = data.getString("timestamp")
                )

                addTrade(trade)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        socket.disconnect()
        socket.close()
    }

    private fun addTrade(trade: Trade) {
        viewModelScope.launch {
            val updatedList = _tradeHistory.value.toMutableList()
            updatedList.add(trade)
            _tradeHistory.value = updatedList
        }
    }

    fun updateSelectedCoin(coin: CoinDetails) {
        selectedCoin.value = coin
    }

    fun setAlgorithmForSelectedCoin() {
        when (selectedCoin.value.symbol) {
            "BTCUSDT" -> {
                rsiLValue.value = 40
                rsiHValue.value = 65
                cciValue.value = -100
                bollingerLbandValue.value = 95000
                bollingerHbandValue.value = 100000
            }

            "ETHUSDT" -> {
                rsiLValue.value = 40
                rsiHValue.value = 70
                bollingerLbandValue.value = 3500
                bollingerHbandValue.value = 4000
                vwapValue.value = 0
            }

            "AVAXUSDT" -> {
                rsiLValue.value = 40
                rsiHValue.value = 65
                cciValue.value = -100
                bollingerLbandValue.value = 45
                bollingerHbandValue.value = 50
            }

            "SOLUSDT" -> {
                rsiLValue.value = 50
                rsiHValue.value = 65
                macdValue.value = 0
                ema20Value.value = 0
                bollingerHbandValue.value = 250
            }

            "RENDERUSDT" -> {
                rsiLValue.value = 65
                rsiHValue.value = 70
                macdValue.value = 0
                ema20Value.value = 0
                bollingerHbandValue.value = 10
            }

            "FETUSDT" -> {
                rsiLValue.value = 30
                rsiHValue.value = 70
                vwapValue.value = 0
                bollingerLbandValue.value = 1
                bollingerHbandValue.value = 2
            }
        }
    }

    fun clearIndicatorValues() {
        rsiLValue.value = 0
        rsiHValue.value = 0
        smaValue.value = 0
        emaValue.value = 0
        macdValue.value = 0
        macdSignalValue.value = 0
        superTrendValue.value = 0
        dmiValue.value = 0
        cciValue.value = 0
        vwapValue.value = 0
        bollingerLbandValue.value = 0
        bollingerHbandValue.value = 0
        ema20Value.value = 0
    }

    fun updateIndicator(value: Int, indicatorName: String) {
        when (indicatorName) {
            "RSI_L" -> rsiLValue.value = value
            "RSI_H" -> rsiHValue.value = value
            "SMA" -> smaValue.value = value
            "EMA" -> emaValue.value = value
            "MACD" -> macdValue.value = value
            "MACD_SIGNAL" -> macdSignalValue.value = value
            "SuperTrend" -> superTrendValue.value = value
            "DMI" -> dmiValue.value = value
            "CCI" -> cciValue.value = value
            "VWAP" -> vwapValue.value = value
            "BOLLINGER_LBAND" -> bollingerLbandValue.value = value
            "BOLLINGER_HBAND" -> bollingerHbandValue.value = value
            "EMA_20" -> ema20Value.value = value
        }
    }

    fun resetBalance() {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.resetAll()
                fetchWallet()
                fetchPrice("BTCUSDT")
            } catch (e: Exception) {
                errorMessage.value = "Reset failed: ${e.message}"
            }
        }
    }

    fun fetchPrice(symbol: String) {
        viewModelScope.launch {
            try {
                val response: Response<CoinDetails> = RetrofitClient.apiService.getCoin(symbol)
                if (response.isSuccessful) {
                    selectedCoin.value = response.body()!!
                } else {
                    errorMessage.value = "Error fetching price: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception fetching price: ${e.message}"
            }
        }
    }

    fun fetchWallet() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getWallet()
                if (response.isSuccessful) {
                    walletData.value = response.body()
                } else {
                    errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception: ${e.message}"
            }
        }
    }

    fun fetchCandlestickData(symbol: String, interval: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getCandlestick(symbol, interval)
                if (response.isSuccessful) {
                    candlestickData.value = response.body() ?: emptyList()
                } else {
                    candlestickData.value = emptyList()
                }
            } catch (e: Exception) {
                candlestickData.value = emptyList()
            }
        }
    }

    fun clearCandlestickData() {
        candlestickData.value = emptyList()
    }

    suspend fun startBot(symbol: String, interval: String, indicators: Map<String, Any>) {
        val request = StartBotRequest(symbol, interval, indicators)

        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.startBot(request)

                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Bot started successfully!"
                    println("Success: $message")
                    isBotStarted.value = true
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error occurred"
                    println("Error: $error")
                    errorMessage.value = "Error starting bot: $error"
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                errorMessage.value = "Exception starting bot: ${e.message}"
            }
        }
    }

    fun stopBot() {
        val request = StopBotRequest(selectedCoin.value.symbol)
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.stopBot(request)
                if (response.isSuccessful) {
                    isBotStarted.value = false
                } else {
                    val error = response.errorBody()?.string() ?: "Unknown error occurred"
                    println("Error: $error")
                }
            } catch (e: Exception) {
                println("Exception: Stopping Bot failed.")
            }
        }
    }

    fun changeBotCondition() {
        isBotStarted.value = !isBotStarted.value
    }

    fun clearTradeHistory() {
        viewModelScope.launch {
            _tradeHistory.value = emptyList()
        }
    }
}