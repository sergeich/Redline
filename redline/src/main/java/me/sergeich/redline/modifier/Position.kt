package me.sergeich.redline.modifier

import android.graphics.Paint
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.TextUnit
import me.sergeich.redline.Axis
import me.sergeich.redline.Defaults
import me.sergeich.redline.Edge
import me.sergeich.redline.SizeUnit
import me.sergeich.redline.components.drawIBeamWithLabel
import me.sergeich.redline.format

/**
 * Visualizes component position relative to its parent with measurement lines and labels.
 *
 * This modifier draws I-beam style measurement lines extending from the component's edges
 * to the parent's boundaries, showing the distances from each edge. This is useful for
 * understanding component positioning and margins within its parent container.
 *
 * @param color The color to use for drawing the position lines. Defaults to [Color.Red].
 * @param textColor The color to use for the position text labels. Defaults to [Color.White].
 * @param textSize The size of the position text labels. Defaults to 14.sp.
 * @param sizeUnit The unit system for displaying measurements. Defaults to [SizeUnit.Dp].
 * @param edges The set of edges to visualize. Defaults to all four edges.
 */
@Stable
public fun Modifier.visualizePosition(
    color: Color = Defaults.color,
    textColor: Color = Defaults.textColor,
    textSize: TextUnit = Defaults.textSize,
    sizeUnit: SizeUnit = Defaults.sizeUnit,
    edges: Set<Edge> = setOf(Edge.Top, Edge.Leading, Edge.Bottom, Edge.Trailing),
): Modifier {
    return this
        .then(
            PositionElement(
                color = color,
                textColor = textColor,
                textSize = textSize,
                sizeUnit = sizeUnit,
                edges = edges
            )
        )
}

private class PositionElement(
    private val color: Color = Color.Unspecified,
    private val textColor: Color,
    private val textSize: TextUnit,
    private val sizeUnit: SizeUnit,
    private val edges: Set<Edge>,
) : ModifierNodeElement<PositionNode>() {

    override fun create(): PositionNode {
        return PositionNode(
            color,
            textColor,
            textSize,
            sizeUnit,
            edges
        )
    }

    override fun update(node: PositionNode) {
        node.color = color
        node.textColor = textColor
        node.textSize = textSize
        node.sizeUnit = sizeUnit
        node.edges = edges
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "position"
            properties["color"] = color
            properties["textColor"] = textColor
            properties["textSize"] = textSize
            properties["sizeUnit"] = sizeUnit
            properties["edges"] = edges.joinToString { it.name }
        }
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + sizeUnit.hashCode()
        result = 31 * result + edges.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? PositionElement ?: return false
        return color == otherModifier.color &&
                textColor == otherModifier.textColor &&
                textSize == otherModifier.textSize &&
                sizeUnit == otherModifier.sizeUnit &&
                edges == otherModifier.edges
    }
}

private class PositionNode(
    var color: Color,
    var textColor: Color,
    var textSize: TextUnit,
    var sizeUnit: SizeUnit,
    var edges: Set<Edge>
) : DrawModifierNode, Modifier.Node(), LayoutAwareModifierNode {

    private val textPaint = Paint().apply {
        this.isAntiAlias = true
    }

    private var distances: Rect? = null

    override fun ContentDrawScope.draw() {
        drawContent()

        textPaint.color = textColor.toArgb()
        textPaint.textSize = textSize.toPx()
        val r = distances
        if (r != null) {
            if (edges.contains(Edge.Leading)) {
                drawIBeamWithLabel(
                    text = r.left.format(sizeUnit, density),
                    textPaint = textPaint,
                    axis = Axis.Horizontal,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(-r.left, size.height / 2f),
                    color = color
                )
            }
            if (edges.contains(Edge.Trailing)) {
                drawIBeamWithLabel(
                    text = r.right.format(sizeUnit, density),
                    textPaint = textPaint,
                    axis = Axis.Horizontal,
                    start = Offset(size.width, size.height / 2f),
                    end = Offset(size.width + r.right, size.height / 2f),
                    color = color
                )
            }
            if (edges.contains(Edge.Top)) {
                drawIBeamWithLabel(
                    text = r.top.format(sizeUnit, density),
                    textPaint = textPaint,
                    axis = Axis.Vertical,
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, -r.top),
                    color = color
                )
            }
            if (edges.contains(Edge.Bottom)) {
                drawIBeamWithLabel(
                    text = r.bottom.format(sizeUnit, density),
                    textPaint = textPaint,
                    axis = Axis.Vertical,
                    start = Offset(size.width / 2f, size.height),
                    end = Offset(size.width / 2f, size.height + r.bottom),
                    color = color
                )
            }
        }
    }

    override fun onPlaced(coordinates: LayoutCoordinates) {
        val parent = coordinates.parentLayoutCoordinates
        if (parent != null) {
            val childBounds = coordinates.boundsInParent()
            val parentSize = parent.size

            val left = childBounds.left
            val top = childBounds.top
            val right = parentSize.width - childBounds.right
            val bottom = parentSize.height - childBounds.bottom
            this@PositionNode.distances = Rect(left, top, right, bottom)
        }
    }
}
