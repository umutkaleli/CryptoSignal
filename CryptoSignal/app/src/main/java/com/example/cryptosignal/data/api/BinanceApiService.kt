package com.example.cryptosignal.data.api

import com.example.cryptosignal.data.model.Candlestick
import com.example.cryptosignal.data.model.CoinDetails
import com.example.cryptosignal.data.model.request.StartBotRequest
import com.example.cryptosignal.data.model.response.StartBotResponse
import com.example.cryptosignal.data.model.request.StopBotRequest
import com.example.cryptosignal.data.model.response.StopBotResponse
import com.example.cryptosignal.data.model.response.WalletResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BinanceApiService {
    @GET("/prices")
    suspend fun getAllPrices(): Response<Map<String, String>>

    @GET("/wallet")
    suspend fun getWallet(): Response<WalletResponse>

    @GET("/coin")
    suspend fun getCoin(@Query("symbol") symbol: String): Response<CoinDetails>

    @POST("/reset")
    suspend fun resetAll()

    @GET("/get-candlestick")
    suspend fun getCandlestick(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String
    ): Response<List<Candlestick>>

    @POST("start-bot")
    suspend fun startBot(@Body request: StartBotRequest): Response<StartBotResponse>

    @POST("stop-bot")
    suspend fun stopBot(@Body request: StopBotRequest): Response<StopBotResponse>
}
