package com.mstf.telegramprofileanimations.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    var headerHeightDelta by remember { mutableStateOf(0f) }

    val minAvatarSize = remember { 50.dp }
    val maxAvatarSize = remember { 100.dp }
    var currentAvatarSize by remember { mutableStateOf(minAvatarSize) }

    var isHeaderExpanded by remember { mutableStateOf(false) }

    val backButtonSize = remember { 50.dp }
    val backButtonPadding = remember { 4.dp }

    val items = remember {
        (1..100).map { "Item #$it" }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y.toInt()
                val newHeaderHeight = currentHeaderHeight + delta.dp.div(
                    if (!isHeaderExpanded) 5 else 15
                )
                val previewHeaderHeight = currentHeaderHeight
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
            .nestedScroll(nestedScrollConnection),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(currentHeaderHeight)
                .background(Color(0xFF2187B9)),
        ) {
            if (!isHeaderExpanded) CollapsedHeaderComposable(
                backButtonPadding,
                backButtonSize,
                headerHeightDelta,
                minHeaderHeight,
                currentAvatarSize
            ) else ExpandedHeaderComposable(
                currentHeaderHeight,
                backButtonSize,
                backButtonPadding
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            items(items) {
                Text(
                    it,
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun BoxScope.CollapsedHeaderComposable(
    backButtonPadding: Dp,
    backButtonSize: Dp,
    headerHeightDelta: Float,
    minHeaderHeight: Dp,
    currentAvatarSize: Dp
) {
    IconButton(
        modifier = Modifier
            .padding(backButtonPadding)
            .size(backButtonSize),
        onClick = {},
        content = {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                null,
                tint = Color.White,
            )
        })

    Row(
        modifier = Modifier
            .offset(
                x = lerp(
                    start = backButtonSize + backButtonPadding.times(3),
                    stop = backButtonPadding,
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
                .size(currentAvatarSize)
                .clip(shape = CircleShape)
        )
        Column {
            Text(
                "Mostafa",
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text("last seen recently", color = Color.White, fontSize = 12.sp)
        }
    }
}

@Composable
private fun BoxScope.ExpandedHeaderComposable(
    headerHeight: Dp,
    backButtonSize: Dp,
    backButtonPadding: Dp,
) {
    Image(
        painter = painterResource(R.drawable.profile),
        null,
        modifier = Modifier
            .fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
    IconButton(
        modifier = Modifier
            .padding(backButtonPadding)
            .size(backButtonSize),
        onClick = {},
        content = {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack,
                null,
                tint = Color.White,
            )
        })

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
        )
        Text("last seen recently", color = Color.White, fontSize = 12.sp)
    }
}