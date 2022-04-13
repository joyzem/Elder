package com.example.elder.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.elder.domain.ElderScreen
import java.util.*

@Composable
fun ElderBottomBar(
    allScreens: List<ElderScreen>,
    onTabSelected: (ElderScreen) -> Unit,
    currentScreen: ElderScreen
) {
    BottomNavigation(
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        allScreens.forEach { screen ->
            BottomNavigationItem(
                selected = currentScreen == screen,
                onClick = { onTabSelected(screen) },
                icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                label = { Text(screen.title.uppercase()) },
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = MaterialTheme.colors.onSurface,
                alwaysShowLabel = true
            )
        }
    }
}