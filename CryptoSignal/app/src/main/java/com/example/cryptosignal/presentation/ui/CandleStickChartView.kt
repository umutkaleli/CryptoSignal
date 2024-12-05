package com.example.cryptosignal.presentation.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cryptosignal.data.model.Candlestick
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry

@Composable
fun CandleStickChartView(data: List<Candlestick>) {
    AndroidView(
        factory = { context ->
            CandleStickChart(context).apply {
                val entries = data.mapIndexed { index, candle ->
                    CandleEntry(
                        index.toFloat(),
                        candle.high,
                        candle.low,
                        candle.open,
                        candle.close
                    )
                }
                val dataSet = CandleDataSet(entries, "Candlestick Chart").apply {
                    color = android.graphics.Color.BLACK
                    shadowColor = android.graphics.Color.GRAY
                    decreasingColor = android.graphics.Color.RED
                    increasingColor = android.graphics.Color.GREEN
                    decreasingPaintStyle = android.graphics.Paint.Style.FILL
                    increasingPaintStyle = android.graphics.Paint.Style.FILL
                    setDrawValues(false)
                }

                this.data = CandleData(dataSet)

                setBackgroundColor(android.graphics.Color.parseColor("#1E2329"))

                // Y Ekseni (Fiyatlar)
                axisRight.apply {
                    textColor = android.graphics.Color.WHITE
                    setDrawGridLines(true)
                }
                axisLeft.isEnabled = false

                // X Ekseni (Zaman)
                xAxis.apply {
                    textColor = android.graphics.Color.WHITE
                    setDrawGridLines(false)
                    setAvoidFirstLastClipping(true)
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

                    xAxis.valueFormatter = com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                        data.map { candle ->
                            val dateFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                            val parsedDate = java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.ENGLISH).parse(candle.open_time)
                            dateFormat.format(parsedDate)
                        }
                    )
                }

                // Grafik ayarları
                description.isEnabled = false
                setDrawGridBackground(false)
                setPinchZoom(true)
                isAutoScaleMinMaxEnabled = true
                legend.apply {
                    textColor = android.graphics.Color.WHITE
                    isEnabled = true
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
