package com.example.jetpackcomposecustomscrollbehavior

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // Gap between image top and scrolling layout.
            val marginTopInPx = with(LocalDensity.current) { 24.dp.toPx() }

            // Image height that acquired in runtime.
            var imageHeightInPx by remember { mutableIntStateOf(0) }

            // Offset value to move layout to top.
            var offset by remember { mutableFloatStateOf(0f) }

            // Max corner radius value to round corners.
            val maxCornerRadiusInPx = with(LocalDensity.current) { 16.dp.toPx() }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val delta = available.y
                        offset = (offset + delta).coerceIn(marginTopInPx, imageHeightInPx.toFloat())
                        return Offset(
                            0f,
                            if (offset < imageHeightInPx && offset > marginTopInPx) delta else 0f
                        )
                    }
                }
            }

            Box(
                modifier = Modifier.nestedScroll(nestedScrollConnection)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.android_image),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFCFFFE2))
                        .aspectRatio(2f)
                        .onGloballyPositioned {
                            // Set the image height when composable positioned.
                            imageHeightInPx = it.size.height

                            // Set the offset according to image height
                            // To move layout to bottom during first composition.
                            offset = imageHeightInPx.toFloat()
                        }
                )

                LazyColumn(
                    // Needs bottom padding because of the gap.
                    contentPadding = PaddingValues(bottom = 36.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .offset {
                            // Pass the offset value to the modifier.
                            IntOffset(0, offset.roundToInt())
                        }
                        .graphicsLayer {
                            // Defer reading state until draw phase.
                            val cornerSize =
                                (1 - offset.normalize(imageHeightInPx.toFloat())) * maxCornerRadiusInPx
                            shape = RoundedCornerShape(
                                topStart = cornerSize,
                                topEnd = cornerSize
                            )
                            clip = true
                        }
                        .background(Color.White)
                ) {
                    // Scrolling layout content
                    item { TitleSection() }
                    item { TextSection() }
                    item { HorizontalRowSection() }
                    item { TextSection() }
                    item { VerticalCardSection() }
                }
            }
        }
    }
}

@Composable
fun TitleSection() {
    Text(
        LoremIpsum(4).values.joinToString(),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)
    )
}

@Composable
fun TextSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 12.dp, start = 24.dp, end = 24.dp)
    ) {

        Text(
            LoremIpsum(100).values.joinToString().replace("\n", " "),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Left
        )

        Text(
            LoremIpsum(50).values.joinToString().replace("\n", " "),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun VerticalCardSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 24.dp)
    ) {
        repeat(3) {
            HorizontalCard()
        }
    }
}

@Composable
fun HorizontalCard() {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(24))
                .background(Color(0xFFFFE6C9))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 24.dp)
            ) {

                Text(
                    text = LoremIpsum(20).values.joinToString().replace("\n", " "),
                    modifier = Modifier.weight(1f)
                )

                Image(
                    painter = painterResource(id = R.drawable.android_logo),
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }
    }
}

@Composable
fun HorizontalRowSection() {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(24.dp)
    ) {
        items(5) {
            Card()
        }
    }
}

@Composable
fun Card() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(24))
            .background(Color(0xFFFFE6C9))
    ) {
        Image(
            painter = painterResource(id = R.drawable.android_logo),
            contentDescription = null
        )
    }
}

// Normalize the value between 0 and 1.
fun Float.normalize(maxValue: Float): Float {
    return if (maxValue == 0f) 0f else this / maxValue
}