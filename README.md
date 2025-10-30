[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/me.sergeich/redline/badge.svg)](https://maven-badges.sml.io/sonatype-central/me.sergeich/redline/)

# Redline for Android

Easy Redlines for Jetpack Compose - Visualize positions, sizes, spacings and alignment guides to verify your implementation against specs or to debug layout problems.

## Features

- **Position Visualization**: Show position of composables relative to their parent
- **Dimension Visualization**: Display width and height measurements with I-Beam indicators
- **Baseline Visualization**: Visualize text baselines for alignment debugging
- **Spacing Visualization**: Measure and display spacing between composables

![Sample Screenshot](/app_sample_screenshot.png)

## Installation

The library is published at the coordinates:
```
implementation 'me.sergeich:redline:0.0.1'
```

## Usage

The library provides modifiers that can be applied to any Composable to visualize layout measurements.

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

### Enums

- `Edge`: Top, Leading, Trailing, Bottom
- `Dimension`: Width, Height
- `Axis`: Horizontal, Vertical
- `LineStyle`: Default, Dashed, Dotted
- `SizeUnit`: Dp, Px

### Modifier Extensions

#### `Modifier.visualizePosition()`
Visualizes component position relative to its parent with measurement lines and labels.

**Parameters:**
- `color: Color = Color.Red` - Color for position lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `edges: Set<Edge> = setOf(Edge.Top, Edge.Leading, Edge.Bottom, Edge.Trailing)` - Edges to visualize

#### `Modifier.visualizeDimension()`
Visualizes component dimensions with measurement lines and labels.

**Parameters:**
- `color: Color = Color.Red` - Color for dimension lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `dimensions: Set<Dimension> = setOf(Dimension.Width, Dimension.Height)` - Dimensions to visualize

#### `Modifier.visualizeSize()`
Convenience function for visualizing both width and height dimensions.

**Parameters:**
- `color: Color = Color.Red` - Color for dimension lines

#### `Modifier.visualizeBaseline()`
Visualizes text baselines with horizontal lines.

**Parameters:**
- `color: Color = Color.Red` - Color for baseline lines

#### `Modifier.measureSpacing()`
Marks composables for spacing measurement. Use with `visualizeSpacing()`.

#### `Modifier.visualizeSpacing()`
Visualizes spacing between marked composables.

**Parameters:**
- `color: Color = Color.Red` - Color for spacing lines
- `textColor: Color = Color.White` - Color for text labels
- `textSize: TextUnit = 14.sp` - Size of text labels
- `sizeUnit: SizeUnit = SizeUnit.Dp` - Unit system for measurements
- `axis: Axis = Axis.Horizontal` - Axis along which to measure spacing

## Examples

See the example app in `app/src/main/java/com/redline/android/example/MainActivity.kt` for comprehensive usage examples.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
