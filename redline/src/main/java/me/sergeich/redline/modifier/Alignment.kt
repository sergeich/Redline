package me.sergeich.redline.modifier

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visualizes text baselines with horizontal lines.
 *
 * This modifier draws horizontal lines to show the first and last baselines of text content,
 * which is useful for understanding text alignment and spacing in UI layouts.
 *
 * @param color The color to use for drawing the baseline lines. Defaults to [Color.Red].
 */
@Stable
public fun Modifier.visualizeBaseline(
    color: Color = Color.Red
): Modifier {
    return this
        .then(
            BaselineElement(
                color = color
            )
        )
}

private class BaselineElement(
    private val color: Color = Color.Unspecified
) : ModifierNodeElement<BaselineNode>() {

    override fun create(): BaselineNode {
        return BaselineNode(color)
    }

    override fun update(node: BaselineNode) {
        node.color = color
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            name = "alignment"
            properties["color"] = color
        }
    }

    override fun hashCode(): Int {
        return color.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? BaselineElement ?: return false
        return color == otherModifier.color
    }
}

private class BaselineNode(
    var color: Color,
) : DrawModifierNode, Modifier.Node(), LayoutModifierNode {

    private var baselineFirst: Int = -1
    private var baselineLast: Int = -1
    private val linePadding = 10.dp

    override fun ContentDrawScope.draw() {
        val linePaddingPx = linePadding.toPx()
        drawContent()

        if (baselineFirst >= 0) {
            drawLine(
                color,
                Offset(-linePaddingPx, baselineFirst.toFloat()),
                Offset(size.width + linePaddingPx, baselineFirst.toFloat())
            )
        }

        if (baselineLast >= 0 && baselineLast != baselineFirst) {
            drawLine(
                color,
                Offset(-linePaddingPx, baselineLast.toFloat()),
                Offset(size.width + linePaddingPx, baselineLast.toFloat())
            )
        }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)

        // Get baseline from the placeable
        baselineFirst = placeable[FirstBaseline]
        baselineLast = placeable[LastBaseline]

        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }
}

@Preview
@Composable
private fun BaselineSample() {

    Row {
        Text(
            "Lorem Ipsum Dolor\nSecond line\nThird line",
            fontSize = 20.sp,
            modifier = Modifier
                .alignByBaseline()
                .visualizeBaseline()
        )
        Spacer(Modifier.size(48.dp))
        Text(
            "Another text",
            fontSize = 12.sp,
            modifier = Modifier
                .alignByBaseline()
                .visualizeBaseline()
        )
    }
}
