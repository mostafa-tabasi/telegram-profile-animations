package com.mstf.telegramprofileanimations.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.mstf.telegramprofileanimations.R

@Composable
fun ProfileScreenWithAnimatedValue(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    val backButtonSize = 60.dp
    val backButtonPadding = 8.dp
    val minHeaderHeight = 60.dp
    val maxHeaderHeight = minHeaderHeight.times(5)
    var headerHeight by remember { mutableStateOf(minHeaderHeight) }
    val animatedHeaderHeight by animateDpAsState(
        targetValue = headerHeight,
        label = "header_height",
        animationSpec = tween(50)
    )
    var headerHeightDelta by remember { mutableFloatStateOf(0f) }
    var headerContainerWidth by remember { mutableStateOf(0.dp) }

    val minProfileSize = 48.dp
    val maxProfileSize = minProfileSize.times(2)
    var profileWidth by remember { mutableStateOf(minProfileSize) }
    val animatedProfileWidth by animateDpAsState(
        targetValue = profileWidth,
        label = "profile_width",
    )
    var profileHeight by remember { mutableStateOf(minProfileSize) }
    val animatedProfileHeight by animateDpAsState(
        targetValue = profileHeight,
        label = "profile_height",
    )

    //                                        phase completion fraction
    fun isHeaderInThirdPhase(): Pair<Boolean, Float> {
        return Pair(
            headerHeight in maxHeaderHeight / 5 * 2..maxHeaderHeight / 5 * 3,
            (headerHeight - (maxHeaderHeight / 5 * 2)) / ((maxHeaderHeight / 5 * 3) - (maxHeaderHeight / 5 * 2))
        )
    }

    fun isHeaderInFourthPhase(): Pair<Boolean, Float> {
        return Pair(
            headerHeight > maxHeaderHeight / 5 * 3,
            ((headerHeight - maxHeaderHeight / 5 * 3) / (maxHeaderHeight - maxHeaderHeight / 5 * 3))
        )
    }

    var isDragging by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (!isDragging) return Offset.Zero
                if (headerHeight <= minHeaderHeight) return Offset.Zero
                if (headerHeight >= maxHeaderHeight) return Offset.Zero
                if (lazyListState.canScrollBackward && delta > 0) return Offset.Zero

                // println("=== onPreScroll, deltaInDp: ${with(density) { delta.toDp() }} / canScrollForward: ${lazyListState.canScrollForward} / canScrollBackward: ${lazyListState.canScrollBackward}")
                return Offset(available.x, available.y)
            }
        }
    }

    var skipDragEventCounter by remember { mutableIntStateOf(0) }
    val dragDetectionModifier = remember {
        Modifier
            .nestedScroll(nestedScrollConnection)
            .pointerInput(true) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Press -> {
                                isDragging = true

                                // println("PointerEventType.Press")
                            }

                            PointerEventType.Release -> {
                                skipDragEventCounter = 0
                                isDragging = false

                                when {
                                    headerHeight >= maxHeaderHeight
                                        .div(5)
                                        .times(2) &&
                                            headerHeight < maxHeaderHeight
                                        .div(5)
                                        .times(3)
                                    -> {
                                        headerHeight = maxHeaderHeight
                                            .div(5)
                                            .times(2)

                                        profileWidth = minProfileSize
                                        profileHeight = minProfileSize
                                    }

                                    headerHeight >= maxHeaderHeight
                                        .div(5)
                                        .times(3)
                                    -> {
                                        headerHeight = maxHeaderHeight

                                        profileWidth = headerContainerWidth
                                        profileHeight = headerHeight
                                    }

                                    else -> {}
                                }

                                // println("PointerEventType.Release")
                            }

                            PointerEventType.Move -> {
                                val delta =
                                    event.changes[0].let { it.position.y - it.previousPosition.y }
                                val deltaInDp = with(density) { delta.toDp() }

                                if (skipDragEventCounter < 3) {
                                    skipDragEventCounter++
                                } else {
                                    if (!lazyListState.canScrollBackward || delta < 0) {
                                        val elasticityLevel =
                                            if (
                                                headerHeight > maxHeaderHeight
                                                    .div(5)
                                                    .times(2) &&
                                                headerHeight < maxHeaderHeight
                                                    .div(5)
                                                    .times(3)
                                            ) 3
                                            else 1

                                        headerHeight =
                                            (headerHeight + deltaInDp.div(elasticityLevel))
                                                .coerceIn(minHeaderHeight, maxHeaderHeight)

                                        headerHeightDelta = (headerHeight - minHeaderHeight) /
                                                (maxHeaderHeight
                                                    .div(5)
                                                    .times(2) - minHeaderHeight)

                                        if (isHeaderInThirdPhase().first) {
                                            val phaseFraction = isHeaderInThirdPhase().second
                                            // println("In third phase, fraction: $phaseFraction")

                                            profileWidth = lerp(
                                                start = minProfileSize,
                                                stop = maxProfileSize,
                                                fraction = phaseFraction,
                                            )
                                            profileHeight = lerp(
                                                start = minProfileSize,
                                                stop = maxProfileSize,
                                                fraction = phaseFraction,
                                            )

                                        } else if (isHeaderInFourthPhase().first) {
                                            profileWidth = headerContainerWidth
                                            profileHeight = headerHeight
                                        }
                                    }
                                }

                                // println("PointerEventType.Move, delta: $delta / deltaInDp: $deltaInDp")
                            }
                        }
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                headerContainerWidth = with(density) { it.width.toDp() }
            },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(animatedHeaderHeight)
                .background(Color.Gray),
        ) {
            Row(
                modifier = Modifier
                    .offset {
                        if (isHeaderInFourthPhase().first) IntOffset(0, 0)
                        else IntOffset(
                            x = with(density) {
                                lerp(
                                    start = backButtonSize,
                                    stop = backButtonPadding,
                                    fraction = headerHeightDelta.coerceIn(0f, 1f),
                                )
                                    .toPx()
                                    .toInt()
                            },
                            y = with(density) {
                                lerp(
                                    start = backButtonPadding,
                                    stop = backButtonSize - backButtonPadding,
                                    fraction = headerHeightDelta.coerceIn(0f, 1f),
                                )
                                    .toPx()
                                    .toInt()
                            }
                        )
                    }
                    .padding(horizontal = backButtonPadding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Image(
                    painter = painterResource(R.drawable.profile),
                    null,
                    modifier = Modifier
                        .width(animatedProfileWidth)
                        .height(animatedProfileHeight)
                        .clip(shape = CircleShape)
                )
                Column {
                    Text(
                        "Mostafa",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "last seen recently",
                        color = Color.White,
                        fontSize = 12.sp,
                    )
                }
            }

            IconButton(
                onClick = {},
                modifier = Modifier.size(backButtonSize),
            ) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
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