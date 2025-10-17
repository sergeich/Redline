package me.sergeich.redline

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import java.util.Locale

/**
 * Represents different edges for position visualization
 */
public enum class Edge {
    Top, Leading, Trailing, Bottom
}

/**
 * Represents different dimensions for visualization
 */
public enum class Dimension {
    Width, Height
}

/**
 * Represents different axes for spacing visualization
 */
public enum class Axis {
    Horizontal, Vertical
}

public enum class LineStyle {
    Default, Dashed, Dotted
}

public enum class SizeUnit {
    Dp, Px
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

internal fun Float.format(sizeUnit: SizeUnit, density: Float): String {
    val value = when (sizeUnit) {
        SizeUnit.Dp -> this / density
        SizeUnit.Px -> this
    }
    return String.format(Locale.ROOT, "%.0f", value)
}
