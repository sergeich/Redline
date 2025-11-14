package me.sergeich.redline.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.TextUnit
import me.sergeich.redline.LocalRedlineConfig
import me.sergeich.redline.RedlineConfig
import me.sergeich.redline.SizeUnit

open class ConfigAwareNode(
    var config: RedlineConfig?
) : Modifier.Node(), CompositionLocalConsumerModifierNode {

    protected val localConfig: RedlineConfig
        get() = config ?: currentValueOf(LocalRedlineConfig)
    protected val color: Color
        get() = localConfig.color
    protected val textColor: Color
        get() = localConfig.textColor
    protected val textSize: TextUnit
        get() = localConfig.textSize
    protected val sizeUnit: SizeUnit
        get() = localConfig.sizeUnit

    protected fun shouldDraw(): Boolean {
        val config = localConfig
        val isInPreview = currentValueOf(LocalInspectionMode)
        return !config.useInPreviewOnly || isInPreview
    }
}
