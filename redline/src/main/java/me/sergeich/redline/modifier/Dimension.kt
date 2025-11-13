package me.sergeich.redline.modifier

import android.graphics.Paint
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import me.sergeich.redline.Axis
import me.sergeich.redline.Defaults
import me.sergeich.redline.Dimension
import me.sergeich.redline.SizeUnit
import me.sergeich.redline.components.drawDimensionLabel
import me.sergeich.redline.components.drawIBeamWithLabel
import me.sergeich.redline.format

/**
 * Visualizes component dimensions with measurement lines and labels.
 *
 * This modifier draws I-beam style measurement lines with dimension labels to show
 * the width and/or height of a component. The measurements are displayed in the
 * specified unit system and can be customized with different colors and text sizes.
 *
 * @param color The color to use for drawing the dimension lines. Defaults to [Color.Red].
 * @param textColor The color to use for the dimension text labels. Defaults to [Color.White].
 * @param textSize The size of the dimension text labels. Defaults to 14.sp.
 * @param sizeUnit The unit system for displaying measurements. Defaults to [SizeUnit.Dp].
 * @param dimensions The set of dimensions to visualize. Defaults to both width and height.
 *
 */
@Stable
public fun Modifier.visualizeDimension(
    color: Color = Defaults.color,
    textColor: Color = Defaults.textColor,
    textSize: TextUnit = Defaults.textSize,
    sizeUnit: SizeUnit = Defaults.sizeUnit,
    dimensions: Set<Dimension> = setOf(Dimension.Width, Dimension.Height)
): Modifier {
    return this.then(
        DimensionElement(
            color = color,
            textColor = textColor,
            textSize = textSize,
            sizeUnit = sizeUnit,
            dimensions = dimensions
        )
    )
}

/**
 * Visualizes both width and height dimensions with measurement lines and labels.
 *
 * This is a convenience function that calls [visualizeDimension] with both width and height
 * dimensions enabled. It provides a simple way to show the complete size of a component.
 *
 * @param color The color to use for drawing the dimension lines. Defaults to [Color.Red].
 */
@Stable
public fun Modifier.visualizeSize(
    color: Color = Defaults.color
): Modifier = visualizeDimension(color = color)

private class DimensionElement(
    private val color: Color = Color.Unspecified,
    private val textColor: Color,
    private val textSize: TextUnit,
    private val sizeUnit: SizeUnit,
    private val dimensions: Set<Dimension>
) : ModifierNodeElement<DimensionsNode>() {

    override fun create(): DimensionsNode {
        return DimensionsNode(
            color,
            textColor,
            textSize,
            sizeUnit,
            dimensions
        )
    }

    override fun update(node: DimensionsNode) {
        node.color = color
        node.textColor = textColor
        node.textSize = textSize
        node.sizeUnit = sizeUnit
        node.dimensions = dimensions
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "dimension"
            properties["color"] = color
            properties["textColor"] = textColor
            properties["textSize"] = textSize
            properties["sizeUnit"] = sizeUnit
            properties["dimensions"] = dimensions.joinToString { it.name }
        }
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + sizeUnit.hashCode()
        result = 31 * result + dimensions.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? DimensionElement ?: return false
        return color == otherModifier.color &&
                textColor == otherModifier.textColor &&
                textSize == otherModifier.textSize &&
                sizeUnit == otherModifier.sizeUnit &&
                dimensions == otherModifier.dimensions
    }
}

private class DimensionsNode(
    var color: Color,
    var textColor: Color,
    var textSize: TextUnit,
    var sizeUnit: SizeUnit,
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
                val text = size.width.format(sizeUnit, density)
                drawIBeamWithLabel(
                    text = text,
                    textPaint = textPaint,
                    axis = Axis.Horizontal,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    color = color
                )
            }

            dimensions.contains(Dimension.Height) -> {
                val text = size.height.format(sizeUnit, density)
                drawIBeamWithLabel(
                    text = text,
                    textPaint = textPaint,
                    axis = Axis.Vertical,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    color = color
                )
            }
        }
    }

    private fun ContentDrawScope.drawSize(textPaint: Paint) {
        drawRect(
            color = color,
            style = Stroke(width = strokeWidthDp.toPx())
        )
        // Draw width label in the corner
        val text = "${size.width.format(sizeUnit, density)}×${size.height.format(sizeUnit, density)}"
        drawDimensionLabel(
            text = text,
            color = color,
            markPoint = Offset(size.width, 0f),
            textPaint = textPaint
        )
    }

}
