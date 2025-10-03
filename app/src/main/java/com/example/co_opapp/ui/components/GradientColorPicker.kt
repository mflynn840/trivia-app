package com.example.co_opapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

@Composable
fun GradientColorPicker(
    gradientColors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    thumbSize: Dp = 28.dp
) {
    var thumbPosition by remember { mutableStateOf(0f) }
    var previewColor by remember { mutableStateOf(selectedColor) }

    BoxWithConstraints(modifier = modifier.height(thumbSize)) {
        val widthPx = constraints.maxWidth.toFloat()

        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(gradientColors))
        )

        // Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbPosition * widthPx).roundToInt(), 0) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(previewColor) // show preview color
                .border(3.dp, Color.Black, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            // commit color when drag ends
                            onColorSelected(previewColor)
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        thumbPosition = (thumbPosition + dragAmount.x / widthPx).coerceIn(0f, 1f)

                        // Interpolate color smoothly
                        val scaledPos = thumbPosition * (gradientColors.size - 1)
                        val index = scaledPos.toInt().coerceIn(0, gradientColors.size - 2)
                        val fraction = scaledPos - index
                        previewColor = lerpColor(gradientColors[index], gradientColors[index + 1], fraction)
                    }
                }
        )
    }
}

// Linear interpolation
fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
