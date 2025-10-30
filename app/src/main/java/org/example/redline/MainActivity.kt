package org.example.redline

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.sergeich.redline.Axis
import me.sergeich.redline.Dimension
import me.sergeich.redline.Edge
import me.sergeich.redline.modifier.measureSpacing
import me.sergeich.redline.modifier.visualizeBaseline
import me.sergeich.redline.modifier.visualizeDimension
import me.sergeich.redline.modifier.visualizePosition
import me.sergeich.redline.modifier.visualizeSize
import me.sergeich.redline.modifier.visualizeSpacing
import org.example.redline.ui.theme.RedlineAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            RedlineAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RedlineExample(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun RedlineExample(modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Redline Modifier API",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text("Size Visualization", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .visualizeSize(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Size Visualization",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }

        Text("Width Dimension", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .visualizeDimension(
                    color = Color.Red,
                    dimensions = setOf(Dimension.Width)
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
                    text = "Width Only",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.offset(y = (-16).dp)
                )
            }
        }

        Text("Height Dimension", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .visualizeDimension(
                    color = Color.Red,
                    dimensions = setOf(Dimension.Height)
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
                    text = "Height Only",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.offset(y = (-16).dp)
                )
            }
        }

        Text("Position Visualization", style = MaterialTheme.typography.titleMedium, color = Color.Black)
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
                    .offset(x = 42.dp, y = 8.dp)
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

        Text("Baseline Visualization", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Row {
            Text(
                "Lorem Ipsum Dolor",
                fontSize = 20.sp,
                modifier = Modifier
                    .alignByBaseline()
                    .visualizeBaseline()
            )
            Spacer(Modifier.size(48.dp))
            Text(
                "Another text",
                fontSize = 16.sp,
                modifier = Modifier
                    .alignByBaseline()
                    .visualizeBaseline()
            )
        }

        Text("Spacing Visualization", style = MaterialTheme.typography.titleMedium, color = Color.Black)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .visualizeSpacing(color = Color.Red, axis = Axis.Vertical),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            repeat(3) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .measureSpacing(),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Item ${index + 1}",
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        // Add some spacing at the end
        Spacer(modifier = Modifier.height(50.dp))
    }
}
