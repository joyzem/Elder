package com.example.elder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class ElderScreen(
    val icon: ImageVector
) {
    Report(
        icon = Icons.Filled.Edit
    ),
    Manage(
        icon = Icons.Filled.Settings
    )
}