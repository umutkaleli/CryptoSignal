package com.example.cryptosignal.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptosignal.data.model.Trade
import com.example.cryptosignal.presentation.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TradeHistoryScreen(viewModel: MainViewModel) {
    val tradeHistory = viewModel.tradeHistory.collectAsState().value

    Scaffold(
        containerColor = Color(0xFFEAECEF),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Action History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Divider(
                    color = Color.Gray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    // Gelen trade'leri listele
                    items(tradeHistory.asReversed()) { trade ->
                        TradeItemRow(trade = trade)
                    }
                }
            }
        }
    )
}

@Composable
fun TradeItemRow(trade: Trade) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = try {
        val date = dateFormat.parse(trade.timestamp)
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Symbol: ${trade.symbol}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Price: ${"%.2f".format(trade.currentPrice)} USDT",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Action: ${trade.results}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(text = "Date: $formattedDate", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Balance: ${"%.2f".format(trade.balance)} USDT",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
