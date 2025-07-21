package com.ad.pocketpilot.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ad.pocketpilot.data.local.MetricType
import com.ad.pocketpilot.viewmodel.AnalyticsViewModel
import kotlin.collections.forEach
import kotlin.math.abs

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsBottomSheet(
    metricType: MetricType,
    viewModel: AnalyticsViewModel = viewModel(),
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val expenses by viewModel.getExpensePerMonth().collectAsState()
    val balance by viewModel.getBalanceOverTime().collectAsState()
    val budget by viewModel.getBudgetStatus().collectAsState()

    Log.d("AnalyticsBottomSheet","expenses: $expenses balance: $balance budget: $budget")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = when (metricType) {
                    MetricType.EXPENSE -> "Monthly Expenses"
                    MetricType.BALANCE -> "Balance Over Time"
                    MetricType.INCOME -> "Budget Usage"
                },
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            when (metricType) {
                MetricType.EXPENSE -> BarChart(data = expenses)
                MetricType.BALANCE -> BarChart(data = balance)
                MetricType.INCOME -> CircularBudgetProgress(
                    total = budget["budget"] ?: 0f,
                    spent = budget["spent"] ?: 1f
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Close")
            }
        }
    }
}*/

@Composable
fun AnalyticBottomSheetContent(
    metricType: MetricType,
    viewModel: AnalyticsViewModel = viewModel()
) {
    LaunchedEffect(metricType) {
        viewModel.loadAnalytics(metricType)
    }
    Column(modifier = Modifier.padding(24.dp)) {
        Text(text = when (metricType) {
            MetricType.BALANCE -> "Balance Over Time"
            MetricType.EXPENSE -> "Expense Over Time"
            MetricType.INCOME -> "Income Over Time"
        },
          style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        val chartData =when(metricType) {
            MetricType.INCOME ->
                viewModel.getIncomeOverTime().collectAsState().value
            MetricType.BALANCE ->
                viewModel.getBalanceOverTime().collectAsState().value
            MetricType.EXPENSE ->
                viewModel.getExpensePerMonth().collectAsState().value
        }

        Log.d("AnalyticBottomSheetContent","Chart Data: ${chartData.values}")
        BarChartView(data = chartData)

    }
}

@Composable
fun CircularBudgetProgress(total: Float, spent: Float) {
    val percent = if(total>0) (spent / total) else 0f
    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = percent,
            strokeWidth = 10.dp,
            color = if (percent < 1f) Color.Green else Color.Red,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = "${(percent * 100).toInt()}% used",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/*
@Composable
fun BarChartView(data: Map<String, Float>) {
    val maxAmount = data.values.maxOrNull() ?: 1f
    val barWidth = 40.dp
    val chartHeight = 150.dp
    val spacing = 16.dp

    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(durationMillis = 800))
    }

    val barColors = listOf(
        Color(0xFF81C784),
        Color(0xFF64B5F6),
        Color(0xFFFFB74D),
        Color(0xFFE57373)
    )

    val monthLabels = data.keys.toList()
    val values = data.values.map { it }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 60.dp), // extra space for labels
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(values.size) { index ->
            val heightRatio = values[index] / maxAmount
            val barHeight = chartHeight * heightRatio

            Column(
                modifier = Modifier
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "₹${values[index].toInt()}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = Color.DarkGray
                )

                // Fixed height container
                Box(
                    modifier = Modifier
                        .height(chartHeight)
                        .width(barWidth),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(barHeight * animatedProgress.value)
                            .clip(RoundedCornerShape(8.dp))
                            .background(barColors[index % barColors.size])
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = monthLabels[index],
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.width(70.dp),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
*/

@Composable
fun BarChartView(data: Map<String, Float>) {
    val maxAbsValue = data.values.maxOfOrNull { abs(it) } ?: 1f
    val barWidth = 40.dp
    val chartHeight = 200.dp
    val spacing = 16.dp

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animatedProgress.animateTo(1f, animationSpec = tween(800))
    }

    val labels = data.keys.toList()
    val values = data.values.map { it }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(chartHeight + 80.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(values.size) { index ->
            val value = values[index]
            val heightRatio = abs(value) / maxAbsValue
            val barHeight = chartHeight * heightRatio * animatedProgress.value

            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "₹${value.toInt()}",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = if (value >= 0) Color(0xFF2E7D32) else Color.Red
                )

                Box(
                    modifier = Modifier
                        .height(chartHeight)
                        .width(barWidth),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .align(if (value >= 0) Alignment.BottomCenter else Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(barHeight)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (value >= 0) Color(0xFF81C784) else Color(0xFFE57373)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = labels[index],
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.width(70.dp),
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/*@Composable
fun BarChartView(data: List<Float>) {
    val max = data.maxOrNull() ?: 1f

    Row(
        modifier = Modifier.fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { value ->
            val heightRatio = value/ max
            Box(modifier = Modifier.width(24.dp)
                .fillMaxHeight(heightRatio)
                .background(Color(0xFF3F51B5), RoundedCornerShape(6.dp))
            )
        }
    }
}*/
/*
@Composable
fun BarChart(
    data: Map<String, Float>,
    barColor: Color = Color(0xFF4CAF50),
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(240.dp)
) {
    val max = data.maxOfOrNull { it.value } ?: 1f
    val keys = data.keys.toList()
    val values = data.values.toList()

    Canvas(modifier = modifier.padding(horizontal = 16.dp)) {
        val barWidth = size.width / (values.size * 2f)
        val space = barWidth

        values.forEachIndexed { index, value ->
            val barHeight = (value / max) * size.height
            val xOffset = index * (barWidth + space)

            drawRoundRect(
                color = barColor,
                topLeft = Offset(xOffset, size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
    }

    // Labels below chart
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        keys.forEach { label ->
            Text(text = label.take(3), style = MaterialTheme.typography.labelSmall)
        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun BottomSheetPreview() {
    AnalyticBottomSheetContent(MetricType.INCOME)
}