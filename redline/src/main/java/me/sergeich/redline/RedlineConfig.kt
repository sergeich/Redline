package me.sergeich.redline

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit

public data class RedlineConfig(
    val color: Color = Defaults.color,
    val textColor: Color = Defaults.textColor,
    val textSize: TextUnit = Defaults.textSize,
    val sizeUnit: SizeUnit = Defaults.sizeUnit,
    val useInPreviewOnly: Boolean = Defaults.useInPreviewOnly,
)

public val LocalRedlineConfig = compositionLocalOf { RedlineConfig() }
