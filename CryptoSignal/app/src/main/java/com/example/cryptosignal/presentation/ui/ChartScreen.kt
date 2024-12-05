package com.example.cryptosignal.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptosignal.presentation.viewmodel.MainViewModel

@Composable
fun ChartScreen(viewModel: MainViewModel) {
    val selectedCoin = viewModel.selectedCoin.value
    val candlestickData = viewModel.candlestickData.value

    var expanded by remember { mutableStateOf(false) }
    val intervals = listOf("1m", "5m", "1h", "4h")
    var selectedInterval by remember { mutableStateOf("1m") }


    LaunchedEffect(selectedCoin.symbol, selectedInterval) {
        viewModel.clearCandlestickData()
        viewModel.fetchCandlestickData(selectedCoin.symbol, selectedInterval)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            shadowElevation = 4.dp
        ) {
            CoinSelectionRow(viewModel)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(contentColor = Color.White)
            ) {
                Text(text = "Interval: $selectedInterval", fontWeight = FontWeight.Bold)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                intervals.forEach { interval ->
                    DropdownMenuItem(
                        text = { Text(interval) },
                        onClick = {
                            selectedInterval = interval
                            expanded = false
                        }
                    )
                }
            }
        }

        if (candlestickData.isNotEmpty()) {
            CandleStickChartView(data = candlestickData)
        } else {
            Text("Loading Chart...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
