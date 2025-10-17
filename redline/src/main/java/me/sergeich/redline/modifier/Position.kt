package me.sergeich.redline.modifier

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sergeich.redline.Axis
import me.sergeich.redline.Edge
import me.sergeich.redline.components.drawDimensionLabel
import me.sergeich.redline.components.drawIBeam
import me.sergeich.redline.components.drawIBeamWithLabel
import me.sergeich.redline.format

/**
 * Draws position with a solid [color] over the content.
 *
 * @param color color to paint position with
 * @param edges desired edges to visualize
 */
@Stable
public fun Modifier.visualizePosition(
    color: Color = Color.Red,
    textColor: Color = Color.White,
    textSize: TextUnit = 14.sp,
    edges: Set<Edge> = setOf(Edge.Top, Edge.Leading, Edge.Bottom, Edge.Trailing),
): Modifier {
    return this
        .then(
            PositionElement(
                color = color,
                textColor = textColor,
                textSize = textSize,
                edges = edges
            )
        )
}

private class PositionElement(
    private val color: Color = Color.Unspecified,
    private val textColor: Color,
    private val textSize: TextUnit,
    private val edges: Set<Edge>,
) : ModifierNodeElement<PositionNode>() {

    override fun create(): PositionNode {
        return PositionNode(
            color,
            textColor,
            textSize,
            edges
        )
    }

    override fun update(node: PositionNode) {
        node.color = color
        node.textColor = textColor
        node.textSize = textSize
        node.edges = edges
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "position"
            properties["color"] = color
            properties["textColor"] = textColor
            properties["textSize"] = textSize
            properties["edges"] = edges.joinToString { it.name }
        }
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + edges.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? PositionElement ?: return false
        return color == otherModifier.color &&
                textColor == otherModifier.textColor &&
                textSize == otherModifier.textSize &&
                edges == otherModifier.edges
    }
}

private class PositionNode(
    var color: Color,
    var textColor: Color,
    var textSize: TextUnit,
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
                    text = r.left.format(),
                    textPaint = textPaint,
                    axis = Axis.Horizontal,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(-r.left, size.height / 2f),
                    color = color
                )
            }
            if (edges.contains(Edge.Trailing)) {
                drawIBeamWithLabel(
                    text = r.right.format(),
                    textPaint = textPaint,
                    axis = Axis.Horizontal,
                    start = Offset(size.width, size.height / 2f),
                    end = Offset(size.width + r.right, size.height / 2f),
                    color = color
                )
            }
            if (edges.contains(Edge.Top)) {
                drawIBeamWithLabel(
                    text = r.top.format(),
                    textPaint = textPaint,
                    axis = Axis.Vertical,
                    start = Offset(size.width / 2f, 0f),
                    end = Offset(size.width / 2f, -r.top),
                    color = color
                )
            }
            if (edges.contains(Edge.Bottom)) {
                drawIBeamWithLabel(
                    text = r.bottom.format(),
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

@Preview
@Composable
private fun PositionPreview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color.Blue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.Center)
                .offset(x = 22.dp, y = 11.dp)
                .visualizePosition(
                    edges = setOf(Edge.Top, Edge.Leading, Edge.Trailing, Edge.Bottom)
                ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Position",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}
