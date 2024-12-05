package com.example.cryptosignal.presentation.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptosignal.R
import com.example.cryptosignal.presentation.ui.navigation.Screen
import com.example.cryptosignal.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(viewModel: MainViewModel) {

    var selectedScreen by remember { mutableStateOf(Screen.Home) }

    LaunchedEffect(Unit) {
        viewModel.fetchPrice("BTCUSDT")
        viewModel.fetchWallet()
    }

    Scaffold(
        containerColor = Color(0xFFEAECEF),
        bottomBar = {
            BottomAppBar {
                NavigationBarItem(
                    selected = selectedScreen == Screen.Home,
                    onClick = { selectedScreen = Screen.Home },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedScreen == Screen.Chart,
                    onClick = { selectedScreen = Screen.Chart },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_candlestick_chart_24),
                            contentDescription = "Wallet"
                        )
                    },
                    label = { Text("Chart") })
                NavigationBarItem(
                    selected = selectedScreen == Screen.History,
                    onClick = { selectedScreen = Screen.History },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "History") },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = selectedScreen == Screen.Wallet,
                    onClick = { selectedScreen = Screen.Wallet },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_wallet_24),
                            contentDescription = "Wallet"
                        )
                    },
                    label = { Text("Wallet") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreen) {
                Screen.Home -> MainContent(viewModel)
                Screen.Chart -> ChartScreen(viewModel)
                Screen.History -> TradeHistoryScreen(viewModel)
                Screen.Wallet -> WalletScreen(viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val walletResponse = viewModel.walletData.value
    val selectedCoin = viewModel.selectedCoin.value
    var expanded by remember { mutableStateOf(false) }
    var selectedInterval by remember { mutableStateOf("1m") }
    val intervals = listOf("1m", "5m", "1h", "1d")
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Gray,
            shadowElevation = 4.dp
        ) {
            CoinSelectionRow(viewModel)
        }

        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (walletResponse != null) {
                Text(
                    "Available USDT: $${"%.2f".format(walletResponse.cash_balance)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }

            Text(
                "Selected Coin: ${selectedCoin.symbol}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Text(
                "Price: ${"%.2f".format(selectedCoin.price)} USD",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Text(
                "Amount: ${"%.3f".format(selectedCoin.amount)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
            )
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
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

                    Button(onClick = { viewModel.clearIndicatorValues() }) {
                        Text(
                            text = "CLEAR INDICATORS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.setAlgorithmForSelectedCoin()

                        }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(text = "SET ALGORITHM", textAlign = TextAlign.Center)
                    }
                }
            }

            Text(
                "Adjust Indicators",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            IndicatorSettings(viewModel)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.isBotStarted.value) Color.Red else Color.Blue),
                    onClick = {
                        var indicators = when (selectedCoin.symbol) {
                            "BTCUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "BOLLINGER_LBAND".toLowerCase() to viewModel.bollingerLbandValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                                "CCI".toLowerCase() to viewModel.cciValue.value,
                            )

                            "ETHUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "BOLLINGER_LBAND".toLowerCase() to viewModel.bollingerLbandValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                                "VWAP".toLowerCase() to viewModel.vwapValue.value,
                            )

                            "AVAXUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "BOLLINGER_LBAND".toLowerCase() to viewModel.bollingerLbandValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                                "CCI".toLowerCase() to viewModel.cciValue.value,
                            )

                            "SOLUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "EMA_20".toLowerCase() to viewModel.ema20Value.value,
                                "MACD".toLowerCase() to viewModel.macdValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                            )

                            "RENDERUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "EMA_20".toLowerCase() to viewModel.ema20Value.value,
                                "MACD".toLowerCase() to viewModel.macdValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                            )

                            "FETUSDT" -> mapOf(
                                "RSI_L".toLowerCase() to viewModel.rsiLValue.value,
                                "RSI_H".toLowerCase() to viewModel.rsiHValue.value,
                                "BOLLINGER_LBAND".toLowerCase() to viewModel.bollingerLbandValue.value,
                                "VWAP".toLowerCase() to viewModel.vwapValue.value,
                                "BOLLINGER_HBAND".toLowerCase() to viewModel.bollingerHbandValue.value,
                            )

                            else -> {
                                mapOf()
                            }
                        }

                        if (!viewModel.isBotStarted.value) {
                            coroutineScope.launch {
                                viewModel.startBot(
                                    symbol = selectedCoin.symbol,
                                    interval = selectedInterval,
                                    indicators = indicators
                                )
                            }
                            Toast.makeText(context, "Bot Executed!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.stopBot()
                            Toast.makeText(context, "Bot Stopped!", Toast.LENGTH_SHORT).show()
                        }
                        viewModel.changeBotCondition()

                    }) {
                    if (!viewModel.isBotStarted.value) {
                        Text("EXECUTE BOT", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    } else {
                        Text("STOP BOT", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}
