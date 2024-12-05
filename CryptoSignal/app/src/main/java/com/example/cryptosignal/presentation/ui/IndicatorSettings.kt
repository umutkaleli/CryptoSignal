package com.example.cryptosignal.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cryptosignal.presentation.viewmodel.MainViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun IndicatorSettings(viewModel: MainViewModel) {
    val selectedCoin = viewModel.selectedCoin.value.symbol
    val indicatorsForCoin = viewModel.coinIndicators[selectedCoin] ?: emptyList()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        indicatorsForCoin.forEach { indicatorName ->
            InputSelector(
                label = indicatorName,
                selectedOption = getIndicatorValue(viewModel, indicatorName),
                onOptionSelected = { newValue -> viewModel.updateIndicator(newValue, indicatorName) },
                isEnabled = true
            )
        }
    }
}

// Belirli bir indikatör için değer döndüren yardımcı fonksiyon
private fun getIndicatorValue(viewModel: MainViewModel, indicatorName: String): Int {
    return when (indicatorName) {
        "RSI_L" -> viewModel.rsiLValue.value
        "RSI_H" -> viewModel.rsiHValue.value
        "SMA" -> viewModel.smaValue.value
        "EMA" -> viewModel.emaValue.value
        "MACD" -> viewModel.macdValue.value
        "MACD_SIGNAL" -> viewModel.macdSignalValue.value
        "SuperTrend" -> viewModel.superTrendValue.value
        "DMI" -> viewModel.dmiValue.value
        "CCI" -> viewModel.cciValue.value
        "VWAP" -> viewModel.vwapValue.value
        "BOLLINGER_LBAND" -> viewModel.bollingerLbandValue.value
        "BOLLINGER_HBAND" -> viewModel.bollingerHbandValue.value
        "EMA_20" -> viewModel.ema20Value.value
        else -> 0
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputSelector(
    label: String,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    isEnabled: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = selectedOption.toString(),
            onValueChange = { newText ->
                newText.toIntOrNull()?.let {
                    onOptionSelected(it)
                }
            },
            label = { Text(label) },
            enabled = isEnabled,
            singleLine = true,
            modifier = Modifier.weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        // Reset Button
        Button(
            onClick = {
                onOptionSelected(0)
            },
            enabled = isEnabled,
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E88E5),
            )
        ) {
            Text(text = "RESET", fontWeight = FontWeight.ExtraBold, color = Color.White)
        }
    }
}
