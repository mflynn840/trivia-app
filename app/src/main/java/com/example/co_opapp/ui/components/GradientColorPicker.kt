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

    // Initialize thumbPosition to match the current selected color
    LaunchedEffect(selectedColor) {
        thumbPosition = gradientColors.indexOfFirst { it == selectedColor }
            .takeIf { it >= 0 }?.toFloat()?.div((gradientColors.size - 1)) ?: 0f
    }

    BoxWithConstraints(modifier = modifier.height(thumbSize)) {
        val widthPx = constraints.maxWidth.toFloat()

        // Gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.horizontalGradient(gradientColors))
        )

        // Draggable Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset((thumbPosition * widthPx).roundToInt(), 0) }
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, Color.Black, CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        thumbPosition = (thumbPosition + dragAmount.x / widthPx).coerceIn(0f, 1f)

                        // Smoothly interpolate color along gradient
                        val scaledPos = thumbPosition * (gradientColors.size - 1)
                        val index = scaledPos.toInt().coerceIn(0, gradientColors.size - 2)
                        val fraction = scaledPos - index
                        val interpolatedColor = lerpColorHSL(gradientColors[index], gradientColors[index + 1], fraction)

                        onColorSelected(interpolatedColor)
                    }
                }
        )
    }
}

fun lerpColorHSL(start: Color, end: Color, fraction: Float): Color {
    val startHSL = FloatArray(3)
    val endHSL = FloatArray(3)
    ColorUtils.colorToHSL(start.toArgb(), startHSL)
    ColorUtils.colorToHSL(end.toArgb(), endHSL)

    val h = startHSL[0] + (endHSL[0] - startHSL[0]) * fraction
    val s = startHSL[1] + (endHSL[1] - startHSL[1]) * fraction
    val l = startHSL[2] + (endHSL[2] - startHSL[2]) * fraction

    return Color(ColorUtils.HSLToColor(floatArrayOf(h, s, l)))
}
