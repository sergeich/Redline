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
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.traverseChildren
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import me.sergeich.redline.Axis
import me.sergeich.redline.SizeUnit
import me.sergeich.redline.components.drawIBeamWithLabel
import me.sergeich.redline.format

/**
 * Marks a component for spacing measurement.
 *
 * This modifier marks a component so that it can be included in spacing calculations
 * when used with [visualizeSpacing]. Components marked with this modifier will have
 * their positions measured relative to other marked components in the same parent.
 */
@Stable
fun Modifier.measureSpacing(): Modifier {
    return this.then(SpacingMarkElement)
}

/**
 * Visualizes spacing between marked components with measurement lines and labels.
 *
 * This modifier measures the spacing between components that have been marked with
 * [measureSpacing] and draws I-beam style measurement lines with labels showing
 * the distances. The spacing can be measured along either horizontal or vertical axes.
 *
 * @param color The color to use for drawing the spacing lines. Defaults to [Color.Red].
 * @param textColor The color to use for the spacing text labels. Defaults to [Color.White].
 * @param textSize The size of the spacing text labels. Defaults to 14.sp.
 * @param sizeUnit The unit system for displaying measurements. Defaults to [SizeUnit.Dp].
 * @param axis The axis along which to measure spacing. Defaults to [Axis.Horizontal].
 */
@Stable
fun Modifier.visualizeSpacing(
    color: Color = Color.Red,
    textColor: Color = Color.White,
    textSize: TextUnit = 14.sp,
    sizeUnit: SizeUnit = SizeUnit.Dp,
    axis: Axis = Axis.Horizontal
): Modifier {
    return this
        .then(
            SpacingElement(
                color = color,
                textColor = textColor,
                textSize = textSize,
                sizeUnit = sizeUnit,
                axis = axis
            )
        )
}

private data object SpacingMarkElement : ModifierNodeElement<SpacingMarkNode>() {

    override fun create(): SpacingMarkNode = SpacingMarkNode()

    override fun update(node: SpacingMarkNode) {}

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "measureSpacing"
        }
    }
}

private class SpacingMarkNode : Modifier.Node(), TraversableNode, LayoutAwareModifierNode {

    var bounds: Rect? = null

    override val traverseKey: String = TRAVERSE_KEY

    override fun onPlaced(coordinates: LayoutCoordinates) {
        super.onPlaced(coordinates)
        bounds = coordinates.boundsInParent()
    }

    companion object {
        const val TRAVERSE_KEY = "me.sergeich.redline.SPACING_TRAVERSAL_NODE_KEY"
    }
}

private class SpacingElement(
    private val color: Color = Color.Red,
    private val textColor: Color = Color.White,
    private val textSize: TextUnit = 14.sp,
    private val sizeUnit: SizeUnit = SizeUnit.Dp,
    private val axis: Axis
) : ModifierNodeElement<SpacingNode>() {

    override fun create(): SpacingNode {
        return SpacingNode(
            color,
            textColor,
            textSize,
            sizeUnit = sizeUnit,
            axis
        )
    }

    override fun update(node: SpacingNode) {
        node.color = color
        node.textColor = textColor
        node.textSize = textSize
        node.sizeUnit = sizeUnit
        node.axis = axis
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "spacing"
            properties["color"] = color
            properties["textColor"] = textColor
            properties["textSize"] = textSize
            properties["sizeUnit"] = sizeUnit
            properties["axis"] = axis.toString()
        }
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + textColor.hashCode()
        result = 31 * result + textSize.hashCode()
        result = 31 * result + sizeUnit.hashCode()
        result = 31 * result + axis.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? SpacingElement ?: return false
        return color == otherModifier.color &&
                textColor == otherModifier.textColor &&
                textSize == otherModifier.textSize &&
                sizeUnit == otherModifier.sizeUnit &&
                axis == otherModifier.axis
    }
}

/**
 * Represents a spacing measurement between two points.
 *
 * This data class holds information about the spacing between two components,
 * including the axis along which the measurement was taken and the start and end points.
 *
 * @param axis The axis along which the spacing was measured.
 * @param start The starting point of the spacing measurement.
 * @param end The ending point of the spacing measurement.
 * @property size The calculated size of the spacing in pixels.
 */
private data class Spacing(
    val axis: Axis,
    val start: Offset,
    val end: Offset
) {
    /** The calculated size of the spacing in pixels. */
    val size = when (axis) {
        Axis.Horizontal -> end.x - start.x
        Axis.Vertical -> end.y - start.y
    }
}

private class SpacingNode(
    var color: Color,
    var textColor: Color,
    var textSize: TextUnit,
    var sizeUnit: SizeUnit,
    var axis: Axis
) : Modifier.Node(), DrawModifierNode {

    private val textPaint = Paint().apply {
        isAntiAlias = true
    }

    override fun ContentDrawScope.draw() {
        drawContent()

        textPaint.color = textColor.toArgb()
        textPaint.textSize = textSize.toPx()

        calculateSpacings().forEach {
            drawIBeamWithLabel(
                text = it.size.format(sizeUnit, density),
                textPaint = textPaint,
                axis = axis,
                start = it.start,
                end = it.end,
                color = color
            )
        }
    }

    private fun calculateSpacings(): List<Spacing> {
        val rects = mutableListOf<Rect>()
        traverseChildren(SpacingMarkNode.TRAVERSE_KEY) {
            if (it is SpacingMarkNode) {
                it.bounds?.let { bounds -> rects.add(bounds) }
            }
            true
        }

        rects.sortBy {
            when (axis) {
                Axis.Horizontal -> it.left
                Axis.Vertical -> it.top
            }
        }
        return rects.zipWithNext { l, r ->
            when (axis) {
                Axis.Horizontal -> Spacing(axis, Offset(l.right, l.height / 2), Offset(r.left, l.height / 2))
                Axis.Vertical -> Spacing(axis, Offset(l.width / 2, l.bottom), Offset(l.width / 2, r.top))
            }
        }
    }
}
