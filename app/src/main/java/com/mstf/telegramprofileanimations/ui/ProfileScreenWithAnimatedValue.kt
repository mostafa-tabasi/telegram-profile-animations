package com.mstf.telegramprofileanimations.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreenWithAnimatedValue(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    val minHeaderHeight = 60.dp
    val maxHeaderHeight = 240.dp
    var headerHeight by remember { mutableStateOf(minHeaderHeight) }
    val animatedHeaderHeight by animateDpAsState(
        targetValue = headerHeight,
        label = "header_height",
    )

    val lazyListState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (headerHeight <= minHeaderHeight) return Offset.Zero
                if (headerHeight >= maxHeaderHeight) return Offset.Zero
                if (lazyListState.canScrollBackward && delta > 0) return Offset.Zero

                println("=== onPreScroll, deltaInDp: ${with(density) { delta.toDp() }} / canScrollForward: ${lazyListState.canScrollForward} / canScrollBackward: ${lazyListState.canScrollBackward}")
                return Offset(available.x, available.y)
            }
        }
    }

    val dragDetectionModifier = remember {
        Modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(true) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> {
                                println("PointerEventType.Press")
                            }

                            PointerEventType.Release -> {
                                println("PointerEventType.Release")
                            }

                            PointerEventType.Move -> {
                                val delta =
                                    event.changes[0].let { it.position.y - it.previousPosition.y }
                                val deltaInDp = with(density) { delta.toDp() }

                                if (!lazyListState.canScrollBackward || delta < 0) {
                                    headerHeight = (headerHeight + deltaInDp)
                                        .coerceIn(minHeaderHeight, maxHeaderHeight)
                                }

                                println("PointerEventType.Move, delta: $delta / deltaInDp: $deltaInDp")
                            }
                        }
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeaderHeight)
                .background(Color.Gray),
        )
        LazyColumn(
            state = lazyListState,
            modifier = dragDetectionModifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            items(20) {
                Text(text = "Item #$it", Modifier.padding(16.dp))
            }
        }
    }

}