// Initial source code: https://www.sinasamaki.com/custom-material-3-sliders-in-jetpack-compose/

package com.example.filmflow.ui.composables

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LineSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    val thumbSize = 32.dp
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val previousSnappedValue = remember { mutableFloatStateOf(value) } // Store the last snapped value
    val interaction = remember { MutableInteractionSource() }

    val stepSize = (valueRange.endInclusive - valueRange.start) / steps
    val animatedFraction by animateFloatAsState(
        targetValue = (value / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy
        ), label = "animatedFraction"
    )

    Slider(
        value = value,
        onValueChange = { newValue ->
            val snappedValue = if (steps > 0) { // Only snap if steps > 0
                ((newValue / stepSize).roundToInt() * stepSize).coerceIn(valueRange)
            } else {
                newValue // If no steps, don't snap
            }

            if (snappedValue != previousSnappedValue.floatValue) {  // ***KEY CHANGE***
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
                previousSnappedValue.floatValue = snappedValue // Update the previous value
                onValueChange(snappedValue)
            }
        },
        modifier = modifier,
        valueRange = valueRange,
        steps = steps,
        interactionSource = interaction,
        thumb = {},
        track = { sliderState ->

            var width by remember { mutableIntStateOf(0) }

            Box(
                Modifier
                    .clearAndSetSemantics { }
                    .height(thumbSize)
                    .fillMaxWidth()
                    .onSizeChanged { width = it.width },
            ) {
                Box(
                    Modifier
                        .zIndex(10f)
                        .align(Alignment.CenterStart)
                        .offset {
                            IntOffset(
                                x = lerp(
                                    start = -(thumbSize / 4).toPx() - 20,
                                    end = width - (thumbSize / 4).toPx() - 20,
                                    t = animatedFraction
                                ).roundToInt(),
                                y = 0
                            )
                        }
                        .size(thumbSize)
                        .padding(5.dp)
                        .shadow(
                            elevation = 5.dp,
                            shape = CircleShape,
                        )
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center,
                ) {
//                    Text(
//                        thumbDisplay(value),
//                        style = MaterialTheme.typography.labelSmall,
//                        color = Color.Black
//                    )
                }

                val strokeColor = MaterialTheme.colorScheme.onSurface
                val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .drawWithCache {
                            onDrawBehind {
                                scale(
                                    scaleY = 1f,
                                    scaleX = if (isLtr) 1f else -1f
                                ) {
                                    drawSliderPath(
                                        fraction = animatedFraction,
                                        color = strokeColor,
                                        steps = sliderState.steps
                                    )
                                }
                            }
                        }
                )
            }
        }
    )
}


fun DrawScope.drawSliderPath(
    fraction: Float,
    color: Color,
    steps: Int,
) {

    val path = Path()
    val activeWidth = size.width * fraction
    val midPointHeight = size.height / 2
    val beyondBounds = size.width * 2


    // Point far beyond the right edge
    path.moveTo(
        x = beyondBounds,
        y = midPointHeight
    )

    // Line to a point far beyond the left edge
    path.lineTo(
        x = -beyondBounds,
        y = midPointHeight
    )

    val variation = .1f

    // Line to a point far beyond the left edge
    path.lineTo(
        x = -beyondBounds,
        y = midPointHeight + variation
    )

    // Line to a point far beyond the right edge
    path.lineTo(
        x = beyondBounds,
        y = midPointHeight + variation
    )

    val exclude = Path().apply {
        addRect(Rect(-beyondBounds, -beyondBounds, 0f, beyondBounds))
        addRect(Rect(size.width, -beyondBounds, beyondBounds, beyondBounds))
    }

    val trimmedPath = Path()
    trimmedPath.op(path, exclude, PathOperation.Difference)

    val pathMeasure = PathMeasure()
    pathMeasure.setPath(trimmedPath, false)

    val graduations = steps + 1
    for (i in 0..graduations) {
        val pos = pathMeasure.getPosition((i / graduations.toFloat()) * pathMeasure.length / 2)
        val height = 10f
        when (i) {
            0, graduations -> drawCircle(
                color = color,
                radius = 10f,
                center = pos
            )

            else -> drawLine(
                strokeWidth = if (pos.x < activeWidth) 4f else 2f,
                color = color,
                start = pos + Offset(0f, height),
                end = pos + Offset(0f, -height),
            )
        }
    }

    clipRect(
        left = -beyondBounds,
        top = -beyondBounds,
        bottom = beyondBounds,
        right = activeWidth,
    ) {
        drawTrimmedPath(trimmedPath, color)
    }
    clipRect(
        left = activeWidth,
        top = -beyondBounds,
        bottom = beyondBounds,
        right = beyondBounds,
    ) {
        drawTrimmedPath(trimmedPath, color.copy(alpha = .2f))
    }

}

fun DrawScope.drawTrimmedPath(path: Path, color: Color) {
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = 10f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
        ),
    )
}

fun lerp(start: Float, end: Float, t: Float) = start + t * (end - start)