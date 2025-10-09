package me.sergeich.redline

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import java.util.Locale

/**
 * Redline - Easy Redlines for Jetpack Compose
 *
 * With Redline, you can quickly visualize positions, sizes, spacings and alignment guides
 * to verify your implementation against specs or to debug layout problems.
 */

// ============================================================================
// CORE TYPES
// ============================================================================

/**
 * Represents different edges for position visualization
 */
enum class Edge {
    Top, Leading, Trailing, Bottom
}

/**
 * Represents different dimensions for visualization
 */
enum class Dimension {
    Width, Height
}

/**
 * Represents different axes for spacing visualization
 */
enum class Axis {
    Horizontal, Vertical
}

/**
 * Redline style for different line styles
 */
enum class LineStyle {
    Default, Dashed, Dotted
}

/**
 * Unit point for positioning within rectangles
 */
internal data class UnitPoint(
    val x: Float,
    val y: Float
) {
    companion object {
        val TopLeading = UnitPoint(0f, 0f)
        val Top = UnitPoint(0.5f, 0f)
        val TopTrailing = UnitPoint(1f, 0f)
        val Leading = UnitPoint(0f, 0.5f)
        val Center = UnitPoint(0.5f, 0.5f)
        val Trailing = UnitPoint(1f, 0.5f)
        val BottomLeading = UnitPoint(0f, 1f)
        val Bottom = UnitPoint(0.5f, 1f)
        val BottomTrailing = UnitPoint(1f, 1f)
    }
}

// ============================================================================
// EXTENSION FUNCTIONS
// ============================================================================

/**
 * Extension function for Offset to add offset values
 */
internal fun Offset.offset(x: Float = 0f, y: Float = 0f): Offset {
    return Offset(this.x + x, this.y + y)
}

/**
 * Extension function for Rect to get point at unit position
 */
internal operator fun Rect.get(unitPoint: UnitPoint): Offset {
    return Offset(
        left + width * unitPoint.x,
        top + height * unitPoint.y
    )
}

internal fun Float.format(): String {
    return String.format(Locale.ROOT, "%.0f", this)
}
