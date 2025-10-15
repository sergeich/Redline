package me.sergeich.redline.components

import android.graphics.Paint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.sergeich.redline.Edge
import me.sergeich.redline.UnitPoint
import me.sergeich.redline.get
import me.sergeich.redline.offset

internal fun DrawScope.drawDimensionLabel(
    text: String,
    color: Color,
    markPoint: Offset,
    textPaint: Paint,
    edge: Edge? = null
) {
    val cornerRadius = 4.dp.toPx()
    val labelPadding = 4.dp.toPx()
    val markPadding = 3.dp.toPx()

    val textWidth = textPaint.measureText(text)
    val width = textWidth + labelPadding * 2
    val height = -textPaint.fontMetrics.top + textPaint.fontMetrics.bottom

    val triangleWidth = 5f * density
    val triangleHeight = 6f * density

    val (x, y) = calculateCoords(markPoint, edge, height, width, triangleWidth, markPadding)
    val path = Path()
    val rect = Rect(x, y, x + width, y + height)

    // Create rounded rectangle
    path.addRoundRect(
        RoundRect(
            rect = rect,
            radiusX = cornerRadius,
            radiusY = cornerRadius
        )
    )

    // Add triangle based on edge
    edge?.let { edge ->
        path.addPath(constructTriangleMark(rect, edge, triangleWidth, triangleHeight))
    }
    drawPath(path, color)

    val textX = x + labelPadding
    val textY = y - textPaint.fontMetrics.top
    drawContext.canvas.nativeCanvas.drawText(
        text,
        textX,
        textY,
        textPaint
    )
}

private fun calculateCoords(
    markPoint: Offset,
    edge: Edge?,
    height: Float,
    width: Float,
    triangleWidth: Float,
    markPadding: Float
): Offset {
    var x = markPoint.x
    var y = markPoint.y
    when (edge) {
        Edge.Top -> {
            x -= width / 2
            y += triangleWidth + markPadding
        }

        Edge.Leading -> {
            x += triangleWidth + markPadding
            y -= height / 2
        }

        Edge.Trailing -> {
            x -= width + triangleWidth + markPadding
            y -= height / 2
        }

        Edge.Bottom -> {
            x -= width / 2
            y -= height + triangleWidth + markPadding
        }

        null -> {
            x -= width
        }
    }
    return Offset(x, y)
}

private fun constructTriangleMark(
    rect: Rect,
    edge: Edge,
    triangleWidth: Float,
    triangleHeight: Float
): Path {
    val triangle = Path()

    val points = when (edge) {
        Edge.Leading -> {
            listOf(
                rect[UnitPoint.Leading].offset(y = triangleWidth),
                rect[UnitPoint.Leading].offset(x = -triangleHeight),
                rect[UnitPoint.Leading].offset(y = -triangleWidth)
            )
        }

        Edge.Trailing -> {
            listOf(
                rect[UnitPoint.Trailing].offset(y = triangleWidth),
                rect[UnitPoint.Trailing].offset(x = triangleHeight),
                rect[UnitPoint.Trailing].offset(y = -triangleWidth)
            )
        }

        Edge.Top -> {
            listOf(
                rect[UnitPoint.Top].offset(x = triangleWidth),
                rect[UnitPoint.Top].offset(y = -triangleHeight),
                rect[UnitPoint.Top].offset(x = -triangleWidth)
            )
        }

        Edge.Bottom -> {
            listOf(
                rect[UnitPoint.Bottom].offset(x = triangleWidth),
                rect[UnitPoint.Bottom].offset(y = triangleHeight),
                rect[UnitPoint.Bottom].offset(x = -triangleWidth)
            )
        }
    }
    triangle.moveTo(points[0].x, points[0].y)
    points.drop(1).forEach { point ->
        triangle.lineTo(point.x, point.y)
    }
    triangle.close()
    return triangle
}

@Preview
@Composable
private fun DimensionLabelPreview() {
    val textPaint = Paint().apply {
        this.color = Color.White.toArgb()
        this.isAntiAlias = true
        this.textSize = 48f
    }
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Yellow)
                .height(100.dp)
                .drawWithContent {
                    drawDimensionLabel(
                        "test Test",
                        color = Color.Red,
                        markPoint = Offset(size.width, 0f),
                        textPaint
                    )
                })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Yellow)
                .height(100.dp)
                .drawWithContent {
                    drawDimensionLabel(
                        "test Test",
                        color = Color.Red,
                        edge = Edge.Trailing,
                        markPoint = Offset(size.width, size.height / 2),
                        textPaint = textPaint
                    )
                })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Yellow)
                .height(100.dp)
                .drawWithContent {
                    drawDimensionLabel(
                        text = "342 px",
                        color = Color.Red,
                        edge = Edge.Top,
                        markPoint = Offset(size.width / 2, 0f),
                        textPaint = textPaint
                    )
                })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Yellow)
                .height(100.dp)
                .drawWithContent {
                    drawDimensionLabel(
                        text = "342 px",
                        color = Color.Red,
                        edge = Edge.Leading,
                        markPoint = Offset(0f, size.height / 2),
                        textPaint = textPaint
                    )
                })
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Color.Yellow)
                .height(100.dp)
                .drawWithContent {
                    drawDimensionLabel(
                        text = "342 px",
                        color = Color.Red,
                        edge = Edge.Bottom,
                        markPoint = Offset(size.width / 2, size.height),
                        textPaint = textPaint
                    )
                })
    }
}
