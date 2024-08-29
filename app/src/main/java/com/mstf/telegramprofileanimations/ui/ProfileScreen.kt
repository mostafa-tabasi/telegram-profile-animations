@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.mstf.telegramprofileanimations.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.mstf.telegramprofileanimations.R

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val minHeaderHeight = remember { 60.dp }
    val maxHeaderHeight = remember { 240.dp }
    var currentHeaderHeight by remember { mutableStateOf(minHeaderHeight) }
    var headerHeightDelta by remember { mutableFloatStateOf(0f) }

    var isHeaderExpanded by remember { mutableStateOf(false) }

    val backButtonSize = remember { 50.dp }
    val backButtonPadding = remember { 4.dp }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y.toInt()
                val newHeaderHeight = currentHeaderHeight + delta.dp.div(
                    if (!isHeaderExpanded) 5 else 15
                )
                currentHeaderHeight = newHeaderHeight.coerceIn(minHeaderHeight, maxHeaderHeight)
                headerHeightDelta =
                    (currentHeaderHeight - minHeaderHeight) / (maxHeaderHeight - minHeaderHeight)
                isHeaderExpanded = headerHeightDelta > 0.5

                return Offset(
                    x = 0f,
                    y = when {
                        currentHeaderHeight == minHeaderHeight && delta < 0 -> 0f
                        currentHeaderHeight == maxHeaderHeight && delta > 0 -> 0f
                        else -> available.y
                    },
                )
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.LightGray.copy(alpha = 0.2f))
            .nestedScroll(nestedScrollConnection),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeaderHeight)
                .background(Color.Gray),
        ) {
            SharedTransitionLayout {
                AnimatedContent(
                    targetState = isHeaderExpanded,
                    label = "header_transition",
                ) {
                    if (it) ExpandedHeaderComposable(
                        currentHeaderHeight,
                        backButtonSize,
                        backButtonPadding,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                    else CollapsedHeaderComposable(
                        backButtonPadding,
                        backButtonSize,
                        headerHeightDelta,
                        minHeaderHeight,
                        animatedVisibilityScope = this@AnimatedContent,
                        sharedTransitionScope = this@SharedTransitionLayout,
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(
                count = 20,
                itemContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .padding(8.dp)
                            .background(Color.White, shape = RoundedCornerShape(8.dp)),
                    )
                }
            )
        }
    }
}

@Composable
private fun BoxScope.CollapsedHeaderComposable(
    backButtonPadding: Dp,
    backButtonSize: Dp,
    headerHeightDelta: Float,
    minHeaderHeight: Dp,
    animatedVisibilityScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .offset(
                    x = lerp(
                        start = backButtonSize + backButtonPadding.times(3),
                        stop = backButtonPadding.times(2),
                        fraction = headerHeightDelta
                            .times(2)
                            .coerceIn(0f, 1f),
                    ),
                    y = lerp(
                        start = 0.dp,
                        stop = backButtonSize + backButtonPadding.times(2),
                        fraction = headerHeightDelta
                            .times(2)
                            .coerceIn(0f, 1f),
                    )
                )
                .padding(horizontal = backButtonPadding)
                .height(minHeaderHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.profile),
                null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape)
                    .sharedBounds(
                        rememberSharedContentState(key = ProfileSharedElementType.Image),
                        animatedVisibilityScope = animatedVisibilityScope,
                        clipInOverlayDuringTransition = OverlayClip(CircleShape),
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        boundsTransform = { _, _ ->
                            spring(stiffness = 500f)
                        },
                    )
            )
            Column {
                Text(
                    "Mostafa",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = ProfileSharedElementType.Name),
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                        ),
                )
                Text(
                    "last seen recently",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = ProfileSharedElementType.LastSeen),
                            animatedVisibilityScope = animatedVisibilityScope,
                            resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                        ),
                )
            }
        }

        IconButton(
            modifier = Modifier
                .padding(backButtonPadding)
                .size(backButtonSize)
                .sharedElement(
                    rememberSharedContentState(key = ProfileSharedElementType.Back),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            onClick = {},
            content = {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    null,
                    tint = Color.White,
                )
            }
        )
    }
}

@Composable
private fun BoxScope.ExpandedHeaderComposable(
    headerHeight: Dp,
    backButtonSize: Dp,
    backButtonPadding: Dp,
    animatedVisibilityScope: AnimatedContentScope,
    sharedTransitionScope: SharedTransitionScope,
) {
    with(sharedTransitionScope) {
        Image(
            painter = painterResource(R.drawable.profile),
            null,
            modifier = Modifier
                .fillMaxSize()
                .sharedBounds(
                    rememberSharedContentState(key = ProfileSharedElementType.Image),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    boundsTransform = { _, _ ->
                        spring(stiffness = 500f)
                    },
                ),
            contentScale = ContentScale.Crop,
        )

        Column(
            modifier = Modifier
                .offset(
                    y = headerHeight - 70.dp
                )
                .padding(16.dp)
        ) {
            Text(
                "Mostafa",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = ProfileSharedElementType.Name),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                    ),
            )
            Text(
                "last seen recently",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = ProfileSharedElementType.LastSeen),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds(),
                    ),
            )
        }

        IconButton(
            modifier = Modifier
                .padding(backButtonPadding)
                .size(backButtonSize)
                .sharedElement(
                    rememberSharedContentState(key = ProfileSharedElementType.Back),
                    animatedVisibilityScope = animatedVisibilityScope,
                ),
            onClick = {},
            content = {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    null,
                    tint = Color.White,
                )
            }
        )
    }
}

enum class ProfileSharedElementType {
    Image,
    Name,
    LastSeen,
    Back,
}