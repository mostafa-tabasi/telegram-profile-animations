package com.mstf.telegramprofileanimations.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.mstf.telegramprofileanimations.R
import kotlinx.coroutines.launch

@Composable
fun ProfileScreenWithAnimatedValue(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    val backButtonSize = 60.dp
    val backButtonPadding = 8.dp
    val minHeaderHeight = 60.dp
    val maxHeaderHeight = minHeaderHeight.times(5)
    val headerHeight = remember { Animatable(minHeaderHeight.value) }
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

    var profileCornerRadius by remember { mutableStateOf(50.dp) }

    var profileInfoHeight by remember { mutableStateOf(0.dp) }
    var profileInfoOffset by remember {
        mutableStateOf(
            IntOffset(
                with(density) {
                    (backButtonSize + backButtonPadding.times(3) + minProfileSize).toPx().toInt()
                },
                with(density) { backButtonPadding.toPx().toInt() },
            )
        )
    }
    val animatedProfileInfoOffset by animateIntOffsetAsState(
        targetValue = profileInfoOffset,
        label = "profile_info",
    )

    //                                         phase completion fraction
    fun isHeaderInSecondPhase(): Pair<Boolean, Float> {
        return Pair(
            headerHeight.dp() > minHeaderHeight && headerHeight.dp() < maxHeaderHeight / 5 * 2,
            (headerHeight.dp() - minHeaderHeight) / ((maxHeaderHeight / 5 * 2) - minHeaderHeight)
        )
    }

    fun isHeaderInThirdPhase(): Pair<Boolean, Float> {
        return Pair(
            headerHeight.dp() in maxHeaderHeight / 5 * 2..maxHeaderHeight / 5 * 3,
            (headerHeight.dp() - (maxHeaderHeight / 5 * 2)) / ((maxHeaderHeight / 5 * 3) - (maxHeaderHeight / 5 * 2))
        )
    }

    fun isHeaderInFourthPhase(): Pair<Boolean, Float> {
        return Pair(
            headerHeight.dp() > maxHeaderHeight / 5 * 3,
            ((headerHeight.dp() - maxHeaderHeight / 5 * 3) / (maxHeaderHeight - maxHeaderHeight / 5 * 3))
        )
    }

    val animatedUsernameFontSize by animateFloatAsState(
        targetValue = if (isHeaderInFourthPhase().first) 24f else 16f,
        label = "username_font_size"
    )

    var isDragging by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                if (!isDragging) return Offset.Zero
                if (headerHeight.dp() <= minHeaderHeight) return Offset.Zero
                if (headerHeight.dp() >= maxHeaderHeight) return Offset.Zero
                if (lazyListState.canScrollBackward && delta > 0) return Offset.Zero

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
                            }

                            PointerEventType.Release -> {
                                skipDragEventCounter = 0
                                isDragging = false

                                when {
                                    headerHeight.dp() >= maxHeaderHeight
                                        .div(5)
                                        .times(2) &&
                                            headerHeight.dp() < maxHeaderHeight
                                        .div(5)
                                        .times(3)
                                    -> {
                                        scope.launch {
                                            headerHeight.animateTo(
                                                maxHeaderHeight
                                                    .value
                                                    .div(5)
                                                    .times(2)
                                            )
                                        }

                                        profileWidth = minProfileSize
                                        profileHeight = minProfileSize

                                        profileInfoOffset = IntOffset(
                                            x = (backButtonPadding.times(4) + minProfileSize)
                                                .toPx()
                                                .toInt(),
                                            y = (backButtonSize - backButtonPadding)
                                                .toPx()
                                                .toInt(),
                                        )
                                    }

                                    headerHeight.dp() >= maxHeaderHeight
                                        .div(5)
                                        .times(3)
                                    -> {
                                        scope.launch {
                                            headerHeight.animateTo(maxHeaderHeight.value)
                                        }

                                        profileWidth = headerContainerWidth
                                        profileHeight = maxHeaderHeight

                                        profileInfoOffset = IntOffset(
                                            x = backButtonPadding
                                                .times(2)
                                                .toPx()
                                                .toInt(),
                                            y = (maxHeaderHeight - profileInfoHeight - backButtonPadding)
                                                .toPx()
                                                .toInt(),
                                        )
                                    }

                                    else -> {}
                                }
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
                                            if (isHeaderInThirdPhase().first) 3f else 1f

                                        scope.launch {
                                            headerHeight.snapTo(
                                                (headerHeight.value +
                                                        deltaInDp.value.div(elasticityLevel))
                                                    .coerceIn(
                                                        minHeaderHeight.value,
                                                        maxHeaderHeight.value
                                                    )
                                            )
                                        }

                                        if (isHeaderInSecondPhase().first) {
                                            val phaseFraction = isHeaderInSecondPhase().second

                                            profileInfoOffset = IntOffset(
                                                x = lerp(
                                                    start = backButtonSize +
                                                            backButtonPadding.times(3) +
                                                            minProfileSize,
                                                    stop = backButtonPadding.times(4) +
                                                            minProfileSize,
                                                    fraction = phaseFraction,
                                                )
                                                    .toPx()
                                                    .toInt(),
                                                y = lerp(
                                                    start = backButtonPadding,
                                                    stop = backButtonSize - backButtonPadding,
                                                    fraction = phaseFraction,
                                                )
                                                    .toPx()
                                                    .toInt(),
                                            )

                                        } else if (isHeaderInThirdPhase().first) {
                                            val phaseFraction = isHeaderInThirdPhase().second

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

                                            val profileSizeDifference =
                                                profileWidth - minProfileSize

                                            profileInfoOffset = IntOffset(
                                                x = (backButtonPadding.times(4) +
                                                        minProfileSize +
                                                        profileSizeDifference)
                                                    .toPx()
                                                    .toInt(),
                                                y = (backButtonSize -
                                                        backButtonPadding +
                                                        profileSizeDifference.div(2))
                                                    .toPx()
                                                    .toInt(),
                                            )

                                        } else if (isHeaderInFourthPhase().first) {
                                            profileWidth = headerContainerWidth
                                            profileHeight = headerHeight.dp()

                                            profileInfoOffset = IntOffset(
                                                x = backButtonPadding
                                                    .times(2)
                                                    .toPx()
                                                    .toInt(),
                                                y = (headerHeight.dp() - profileInfoHeight - backButtonPadding)
                                                    .toPx()
                                                    .toInt(),
                                            )
                                        }

                                        profileCornerRadius =
                                            if (isHeaderInFourthPhase().first) 0.dp else 50.dp
                                    }
                                }
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
        val isHeaderInSecondPhase = isHeaderInSecondPhase()
        val isHeaderInThirdPhase = isHeaderInThirdPhase()
        val isHeaderInFourthPhase = isHeaderInFourthPhase()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight.dp())
                .background(Color.Gray),
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                null,
                modifier = Modifier
                    .padding(horizontal = if (isHeaderInFourthPhase.first) 0.dp else backButtonPadding)
                    .offset {
                        if (isHeaderInFourthPhase.first) IntOffset(0, 0)
                        else IntOffset(
                            x = with(density) {
                                lerp(
                                    start = backButtonSize,
                                    stop = backButtonPadding,
                                    fraction = isHeaderInSecondPhase.second.coerceIn(0f, 1f),
                                )
                                    .toPx()
                                    .toInt()
                            },
                            y = with(density) {
                                lerp(
                                    start = backButtonPadding,
                                    stop = backButtonSize - backButtonPadding,
                                    fraction = isHeaderInSecondPhase.second.coerceIn(0f, 1f),
                                )
                                    .toPx()
                                    .toInt()
                            }
                        )
                    }
                    .width(
                        if (
                            (isHeaderInThirdPhase.first && isHeaderInThirdPhase.second > 0.9) ||
                            (isHeaderInFourthPhase.first && isHeaderInFourthPhase.second < 0.1)
                        ) animatedProfileWidth
                        else profileWidth
                    )
                    .height(
                        if (
                            (isHeaderInThirdPhase.first && isHeaderInThirdPhase.second > 0.9) ||
                            (isHeaderInFourthPhase.first && isHeaderInFourthPhase.second < 0.1)
                        ) animatedProfileHeight
                        else profileHeight
                    )
                    .clip(shape = RoundedCornerShape(profileCornerRadius)),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier
                    .onSizeChanged {
                        profileInfoHeight = with(density) { it.height.toDp() }
                    }
                    .offset {
                        if (
                            (isHeaderInThirdPhase.first && isHeaderInThirdPhase.second > 0.9) ||
                            (isHeaderInFourthPhase.first && isHeaderInFourthPhase.second < 0.1) ||
                            !isDragging
                        ) animatedProfileInfoOffset
                        else profileInfoOffset
                    }
            ) {
                Text(
                    "Mostafa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = animatedUsernameFontSize.sp
                )
                Text(
                    "last seen recently",
                    color = Color.White,
                    fontSize = 12.sp,
                )
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

private fun Animatable<Float, AnimationVector1D>.dp(): Dp = value.dp