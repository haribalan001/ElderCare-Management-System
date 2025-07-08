package com.example.demoapp.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun DemoAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // You can customize the theme colors, typography, etc. here if needed.
        content = content
    )
}
