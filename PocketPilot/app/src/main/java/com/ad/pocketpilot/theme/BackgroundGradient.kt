package com.ad.pocketpilot.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.ad.pocketpilot.ui.theme.Almond
import com.ad.pocketpilot.ui.theme.Cream


@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Almond, Cream),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
            .padding(bottom = 8.dp) // Optional padding
    ) {
        content()
    }
}
