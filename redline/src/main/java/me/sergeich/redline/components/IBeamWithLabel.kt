package me.sergeich.redline.components

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.sergeich.redline.Axis
import me.sergeich.redline.Edge
import me.sergeich.redline.LineStyle
import kotlin.math.max
import kotlin.math.min

internal fun DrawScope.drawIBeamWithLabel(
    text: String,
    textPaint: Paint,
    axis: Axis,
    start: Offset,
    end: Offset,
    color: Color,
    style: LineStyle = LineStyle.Default
) {
    drawIBeam(axis, start, end, color, style)
    val edge: Edge
    val markPoint: Offset
    when (axis) {
        Axis.Horizontal -> {
            edge = Edge.Top
            val startX = min(start.x, end.x)
            val endX = max(start.x, end.x)
            markPoint = Offset(endX - (endX - startX) / 2f, start.y)
        }
        Axis.Vertical -> {
            edge = Edge.Leading
            val startY = min(start.y, end.y)
            val endY = max(start.y, end.y)
            markPoint = Offset(start.x, endY - (endY - startY) / 2f)
        }
    }
    drawDimensionLabel(text, color, markPoint, textPaint, edge)
}

@Preview
@Composable
private fun IBeamPreview() {
    val textPaint = Paint().apply {
        isAntiAlias = true
        textSize = 42f
        color = Color.White.toArgb()
    }
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .drawWithContent {
                    drawIBeamWithLabel(
                        text = size.width.toString(),
                        textPaint = textPaint,
                        axis = Axis.Horizontal,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        color = Color.Red
                    )
                })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .drawWithContent {
                    drawIBeamWithLabel(
                        text = size.width.toString(),
                        textPaint = textPaint,
                        axis = Axis.Vertical,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        color = Color.Red
                    )
                })
    }
}
