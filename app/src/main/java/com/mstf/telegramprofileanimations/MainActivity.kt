package com.mstf.telegramprofileanimations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mstf.telegramprofileanimations.ui.ProfileScreenWithAnimatedValue
import com.mstf.telegramprofileanimations.ui.theme.TelegramProfileAnimationsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelegramProfileAnimationsTheme {
                ProfileScreenWithAnimatedValue()
            }
        }
    }
}