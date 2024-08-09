package com.mstf.telegramprofileanimations.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.mstf.telegramprofileanimations.R


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