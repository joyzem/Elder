package com.example.elder.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.ui.graphics.vector.ImageVector

enum class ElderScreen(
    val title: String,
    val icon: ImageVector
) {
    Report(
        title = "Посещаемость",
        icon = Icons.Filled.Edit
    ),
    Manage(
        title = "Группа",
        icon = Icons.Filled.Group
    )
}