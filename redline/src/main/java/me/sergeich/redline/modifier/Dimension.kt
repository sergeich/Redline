package me.sergeich.redline.modifier

import android.graphics.Paint
import android.graphics.PointF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sergeich.redline.Axis
import me.sergeich.redline.Dimension
import me.sergeich.redline.Edge
import me.sergeich.redline.components.drawDimensionLabel
import me.sergeich.redline.components.drawIBeam
import me.sergeich.redline.format

/**
 * Draws dimensions with a solid [color] over the content.
 *
 * @param color color to paint dimensions with
 * @param dimensions desired dimensions to visualize
 */
@Stable
public fun Modifier.visualizeDimension(
    color: Color = Color.Red,
    textColor: Color = Color.White,
    textSize: TextUnit = 14.sp,
    dimensions: Set<Dimension> = setOf(Dimension.Width, Dimension.Height)
): Modifier {
    return this.then(
        DimensionElement(
            color = color,
            textColor = textColor,
            textSize = textSize,
            dimensions = dimensions
        )
    )
}

/**
 * Draws size with a solid [color] over the content.
 *
 * @param color color to paint size with
 */
@Stable
public fun Modifier.visualizeSize(
    color: Color = Color.Red
): Modifier = visualizeDimension(color = color)

private class DimensionElement(
    private val color: Color = Color.Unspecified,
    private val textColor: Color,
    private val textSize: TextUnit,
    private val dimensions: Set<Dimension>
) : ModifierNodeElement<DimensionsNode>() {

    override fun create(): DimensionsNode {
        return DimensionsNode(
            color,
            textColor,
            textSize,
            dimensions
        )
    }

    override fun update(node: DimensionsNode) {
        node.color = color
        node.textColor = textColor
        node.textSize = textSize
        node.dimensions = dimensions
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "dimension"
            properties["color"] = color
            properties["textColor"] = textColor
            properties["textSize"] = textSize
            properties["dimensions"] = dimensions.joinToString { it.name }
        }
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + dimensions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? DimensionElement ?: return false
        return color == otherModifier.color &&
                textColor == otherModifier.textColor &&
                textSize == otherModifier.textSize &&
                dimensions == otherModifier.dimensions
    }
}

private class DimensionsNode(
    var color: Color,
    var textColor: Color,
    var textSize: TextUnit,
    var dimensions: Set<Dimension>
) : DrawModifierNode, Modifier.Node() {

    private val strokeWidthDp = 1.dp
    private val textPaint = Paint().apply {
        isAntiAlias = true
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        textPaint.color = textColor.toArgb()
        textPaint.textSize = textSize.toPx()
        when {
            dimensions.contains(Dimension.Width) && dimensions.contains(Dimension.Height) -> {
                drawSize(textPaint)
            }

            dimensions.contains(Dimension.Width) -> {
                drawIBeam(
                    axis = Axis.Horizontal,
                    start = PointF(0f, size.height / 2),
                    end = PointF(size.width, size.height / 2),
                    color = color
                )
                val text = size.width.format()
                drawDimensionLabel(text = text,
                    color = color,
                    markPoint = PointF(size.width / 2, size.height / 2),
                    edge = Edge.Top,
                    textPaint = textPaint)
            }

            dimensions.contains(Dimension.Height) -> {
                Offset(size.width / 2, 0f)
                drawIBeam(
                    axis = Axis.Vertical,
                    start = PointF(size.width / 2, 0f),
                    end = PointF(size.width / 2, size.height),
                    color = color
                )
                val text = size.height.format()
                drawDimensionLabel(text = text,
                    color = color,
                    markPoint = PointF(size.width / 2, size.height / 2),
                    edge = Edge.Leading,
                    textPaint = textPaint)
            }
        }
    }

    private fun ContentDrawScope.drawSize(textPaint: Paint) {
        drawRect(
            color = color,
            style = Stroke(width = strokeWidthDp.toPx())
        )
        // Draw width label in the corner
        val text = "${size.width.format()}×${size.height.format()}"
        drawDimensionLabel(text = text,
            color = color,
            markPoint = PointF(size.width, 0f),
            textPaint = textPaint)
    }

}

@Preview
@Composable
private fun DimensionPreview() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .visualizeSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .visualizeDimension(dimensions = setOf(Dimension.Height))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .visualizeDimension(dimensions = setOf(Dimension.Width))
        )
    }
}
