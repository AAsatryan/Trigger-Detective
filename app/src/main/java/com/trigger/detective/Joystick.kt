package com.trigger.detective

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt


private const val BASE_RADIUS = 120f
private const val KNOB_RADIUS = 60f

@Composable
fun Joystick(
    modifier: Modifier = Modifier,
    onMove: (dx: Float, dy: Float) -> Unit
) {

    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(180.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        val newOffset = knobOffset + Offset(dragAmount.x, dragAmount.y)

                        // Clamp the knob inside the base circle
                        val distance = sqrt(newOffset.x * newOffset.x + newOffset.y * newOffset.y)
                        knobOffset = if (distance > BASE_RADIUS - KNOB_RADIUS) {
                            val scale = (BASE_RADIUS - KNOB_RADIUS) / distance
                            Offset(newOffset.x * scale, newOffset.y * scale)
                        } else {
                            newOffset
                        }

                        // Send normalized values -1..1
                        onMove(knobOffset.x / (BASE_RADIUS - KNOB_RADIUS), knobOffset.y / (BASE_RADIUS - KNOB_RADIUS))
                    },
                    onDragEnd = {
                        knobOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)

            // Draw base
            drawCircle(color = Color.Gray.copy(alpha = 0.3f), radius = BASE_RADIUS)

            // Draw knob
            drawCircle(color = Color.DarkGray, radius = KNOB_RADIUS, center = center + knobOffset)
        }
    }
}
