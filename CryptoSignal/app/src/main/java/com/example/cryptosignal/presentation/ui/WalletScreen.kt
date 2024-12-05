package com.example.cryptosignal.presentation.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptosignal.presentation.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(viewModel: MainViewModel) {
    val walletResponse = viewModel.walletData.value
    val error = viewModel.errorMessage.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Your Wallet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (walletResponse != null) {
            Text(
                "Available USDT: $${"%.2f".format(walletResponse.cash_balance)}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        // Reset butonu
        Button(
            onClick = {
                viewModel.resetBalance()
                viewModel.clearTradeHistory()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("RESET ALL", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        LaunchedEffect(Unit) {
            viewModel.fetchWallet()
        }

        if (walletResponse != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .weight(1f)
            ) {
                LazyColumn {
                    items(walletResponse.details) { coin ->
                        WalletItem(coin = coin.symbol, balance = coin.amount)
                    }
                }
            }
            Text(
                text = "Total Profit: $${"%.2f".format(walletResponse.total_balance - 10000.0)}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (error != null) {
            Text("Error: $error")
        } else {
            Text("Loading...")
        }
    }
}

@Composable
fun WalletItem(coin: String, balance: Double) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$coin  :    ",
                color = Color.Black,
                style = TextStyle(fontStyle = FontStyle.Italic, fontWeight = FontWeight.ExtraBold)
            )
            Text("%.3f".format(balance))
        }
    }
}
