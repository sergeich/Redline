[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/me.sergeich/redline/badge.svg)](https://maven-badges.sml.io/sonatype-central/me.sergeich/redline/)

# Redline for Android

Easy Redlines for Jetpack Compose - Visualize positions, sizes, spacings, and alignment guides to verify your implementation against specs or to debug layout problems.

Inspired by https://github.com/robb/Redline

## Features

- **Position Visualization**: Show position of composables relative to their parent
- **Dimension Visualization**: Display width and height measurements with I-Beam indicators
- **Baseline Visualization**: Visualize text baselines for alignment debugging
- **Spacing Visualization**: Measure and display spacing between composables

![Sample Screenshot](/app_sample_screenshot.png)

## Installation

The library is published at the coordinates:
```
implementation 'me.sergeich:redline:0.0.2'
```

## Usage

The library provides modifiers that can be applied to any Composable to visualize layout measurements.

### Global Configuration

You can configure redline visualization globally using `RedlineConfig` and `LocalRedlineConfig`:

```kotlin
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import me.sergeich.redline.LocalRedlineConfig
import me.sergeich.redline.RedlineConfig
import me.sergeich.redline.SizeUnit

CompositionLocalProvider(
    LocalRedlineConfig provides RedlineConfig(
        color = Color.Blue,
        textColor = Color.White,
        textSize = 12.sp,
        sizeUnit = SizeUnit.Dp,
        useInPreviewOnly = false // Set to true to only show redlines in preview mode
    )
) {
    // Your composables here
    // All redline modifiers will use the global config unless overridden
}
```

The `useInPreviewOnly` flag is particularly useful for development - when set to `true`, redlines will only appear in Android Studio Preview, not in the running app.

### Position Visualization

```kotlin
Card(
    modifier = Modifier
        .size(100.dp)
        .background(Color.Blue)
        .visualizePosition(
            color = Color.Red,
            edges = setOf(Edge.Top, Edge.Leading, Edge.Trailing, Edge.Bottom)
        )
) {
    Text("Content")
}
```

### Dimension Visualization

```kotlin
// Show both width and height
Card(
    modifier = Modifier
        .size(200.dp, 100.dp)
        .background(Color.Blue)
        .visualizeDimension(
            color = Color.Red,
            dimensions = setOf(Dimension.Width, Dimension.Height)
        )
) {
    Text("Content")
}

// Show only width
Card(
    modifier = Modifier
        .fillMaxWidth()
        .height(80.dp)
        .background(Color.Blue)
        .visualizeDimension(
            color = Color.Cyan,
            dimensions = setOf(Dimension.Width)
        )
) {
    Text("Content")
}

// Convenience function for both dimensions
Card(
    modifier = Modifier
        .size(150.dp)
        .background(Color.Blue)
        .visualizeSize(color = Color.Green)
) {
    Text("Content")
}
```

### Baseline Visualization

```kotlin
Text(
    text = "Sample Text",
    modifier = Modifier
        .visualizeBaseline(color = Color.Red)
)
```

### Spacing Visualization

```kotlin
Column(
    modifier = Modifier
        .padding(16.dp)
        .visualizeSpacing(
            color = Color.Red,
            axis = Axis.Vertical
        )
) {
    repeat(3) { index ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .measureSpacing() // Mark this component for spacing measurement
        ) {
            Text("Item ${index + 1}")
        }
    }
}
```

## API Reference

### Configuration

#### `RedlineConfig`
Data class for configuring redline visualization settings globally or per modifier.

**Properties:**
- `color: Color = Color.Red` - Color for visualization lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `useInPreviewOnly: Boolean = false` - If true, redlines only appear in Compose Preview

#### `LocalRedlineConfig`
CompositionLocal that provides `RedlineConfig` throughout the composition tree. Modifiers will use this config unless a specific config is provided.

### Enums

- `Edge`: Top, Leading, Trailing, Bottom
- `Dimension`: Width, Height
- `Axis`: Horizontal, Vertical
- `LineStyle`: Default, Dashed, Dotted
- `SizeUnit`: Dp, Px

### Modifier Extensions

#### `Modifier.visualizePosition()`
Visualizes component position relative to its parent with measurement lines and labels.

**Overload 1 - Individual parameters:**
- `color: Color = Color.Red` - Color for position lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `useInPreviewOnly: Boolean = false` - If true, only show in preview mode
- `edges: Set<Edge> = setOf(Edge.Top, Edge.Leading, Edge.Bottom, Edge.Trailing)` - Edges to visualize

**Overload 2 - Config object:**
- `config: RedlineConfig? = null` - Optional config object. If null, uses `LocalRedlineConfig`
- `edges: Set<Edge> = setOf(Edge.Top, Edge.Leading, Edge.Bottom, Edge.Trailing)` - Edges to visualize

#### `Modifier.visualizeDimension()`
Visualizes component dimensions with measurement lines and labels.

**Overload 1 - Individual parameters:**
- `color: Color = Color.Red` - Color for dimension lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `useInPreviewOnly: Boolean = false` - If true, only show in preview mode
- `dimensions: Set<Dimension> = setOf(Dimension.Width, Dimension.Height)` - Dimensions to visualize

**Overload 2 - Config object:**
- `config: RedlineConfig? = null` - Optional config object. If null, uses `LocalRedlineConfig`
- `dimensions: Set<Dimension> = setOf(Dimension.Width, Dimension.Height)` - Dimensions to visualize

#### `Modifier.visualizeSize()`
Convenience function for visualizing both width and height dimensions.

**Overload 1 - Individual parameters:**
- `color: Color = Color.Red` - Color for dimension lines

**Overload 2 - Config object:**
- `config: RedlineConfig? = null` - Optional config object. If null, uses `LocalRedlineConfig`

#### `Modifier.visualizeBaseline()`
Visualizes text baselines with horizontal lines.

**Overload 1 - Individual parameters:**
- `color: Color = Color.Red` - Color for baseline lines

**Overload 2 - Config object:**
- `config: RedlineConfig? = null` - Optional config object. If null, uses `LocalRedlineConfig`

#### `Modifier.measureSpacing()`
Marks composables for spacing measurement. Use with `visualizeSpacing()`.

#### `Modifier.visualizeSpacing()`
Visualizes spacing between marked composables.

**Overload 1 - Individual parameters:**
- `color: Color = Color.Red` - Color for spacing lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `useInPreviewOnly: Boolean = false` - If true, only show in preview mode
- `axis: Axis = Axis.Horizontal` - Axis along which to measure spacing

**Overload 2 - Config object:**
- `config: RedlineConfig? = null` - Optional config object. If null, uses `LocalRedlineConfig`
- `axis: Axis = Axis.Horizontal` - Axis along which to measure spacing

## Examples

See the example app in `app/src/main/java/org/example/redline/MainActivity.kt` for comprehensive usage examples.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
