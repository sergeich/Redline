package me.sergeich.redline

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

internal object Defaults {
    val color: Color = Color.Red
    val textColor: Color = Color.White
    val textSize: TextUnit = 14.sp
    val sizeUnit: SizeUnit = SizeUnit.Dp
    val useInPreviewOnly: Boolean = false
}
