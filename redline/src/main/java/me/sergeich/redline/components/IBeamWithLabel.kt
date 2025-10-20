package me.sergeich.redline.components

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
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
