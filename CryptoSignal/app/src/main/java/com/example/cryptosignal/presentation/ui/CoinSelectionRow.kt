package com.example.cryptosignal.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cryptosignal.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinSelectionRow(viewModel: MainViewModel) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        viewModel.walletData.value?.let {
            items(it.details) { coin ->
                FilterChip(
                    selected = viewModel.selectedCoin.value.symbol == coin.symbol,
                    onClick = { viewModel.updateSelectedCoin(coin)
                        viewModel.fetchPrice(coin.symbol)},
                    label = { Text(coin.symbol, fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF4CAF50),
                        selectedLabelColor = Color.Black,
                        containerColor = Color.Transparent,
                        labelColor = Color.Black
                    )
                )
            }
        }
    }
}
