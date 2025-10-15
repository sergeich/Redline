package me.sergeich.redline.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.sergeich.redline.Axis
import me.sergeich.redline.LineStyle
import kotlin.math.max
import kotlin.math.min

internal fun DrawScope.drawIBeam(
    axis: Axis,
    start: Offset,
    end: Offset,
    color: Color,
    style: LineStyle = LineStyle.Default
) {
    val strokeWidth = 1.dp.toPx()
    val endcapSize = 15.dp.toPx()
    val strokeStyle = when (style) {
        LineStyle.Dashed -> Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
        )

        LineStyle.Dotted -> Stroke(
            width = strokeWidth,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 3f))
        )

        else -> Stroke(width = strokeWidth)
    }

    // Draw endcaps
    val endcapPath = Path()
    when (axis) {
        Axis.Horizontal -> {
            endcapPath.moveTo(start.x, start.y - endcapSize / 2f)
            endcapPath.lineTo(start.x, start.y + endcapSize / 2f)
            endcapPath.moveTo(end.x, end.y - endcapSize / 2f)
            endcapPath.lineTo(end.x, end.y + endcapSize / 2f)
        }

        Axis.Vertical -> {
            endcapPath.moveTo(start.x - endcapSize / 2f, start.y)
            endcapPath.lineTo(start.x + endcapSize / 2f, start.y)
            endcapPath.moveTo(end.x - endcapSize / 2f, end.y)
            endcapPath.lineTo(end.x + endcapSize / 2f, end.y)
        }
    }
    drawPath(endcapPath, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    // Draw crossbar
    val crossbarPath = Path().apply {
        moveTo(start.x, start.y)
        lineTo(end.x, end.y)
    }
    drawPath(crossbarPath, color, style = strokeStyle)

    // Draw arrows
    val arrowPath = Path()
    val d = 5.dp.toPx()
    if (size.width > d && size.height > d) {
        when (axis) {
            Axis.Horizontal -> {
                val leftX = min(start.x, end.x)
                val rightX = max(start.x, end.x)
                val y = start.y

                // Left arrow
                arrowPath.moveTo(leftX, y)
                arrowPath.lineTo(leftX + d, y - d / 2f)
                arrowPath.lineTo(leftX + d, y + d / 2f)
                arrowPath.close()

                // Right arrow
                arrowPath.moveTo(rightX, y)
                arrowPath.lineTo(rightX - d, y + d / 2f)
                arrowPath.lineTo(rightX - d, y - d / 2f)
                arrowPath.close()
            }

            Axis.Vertical -> {
                val topY = min(start.y, end.y)
                val bottomY = max(start.y, end.y)
                val x = start.x
                // Top arrow
                arrowPath.moveTo(x, topY)
                arrowPath.lineTo(x - d / 2f, topY + d)
                arrowPath.lineTo(x + d / 2f, topY + d)
                arrowPath.close()

                // Bottom arrow
                arrowPath.moveTo(x, bottomY)
                arrowPath.lineTo(x + d / 2f, bottomY - d)
                arrowPath.lineTo(x - d / 2f, bottomY - d)
                arrowPath.close()
            }
        }
    }
    drawPath(arrowPath, color)
}

@Preview
@Composable
private fun IBeamPreview() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .drawWithContent {
                    drawIBeam(
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
                    drawIBeam(
                        axis = Axis.Vertical,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        color = Color.Red
                    )
                })
    }
}
