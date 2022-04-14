package com.example.elder.ui.components

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.elder.domain.ElderScreen

@Composable
fun ElderBottomBar(
    allScreens: List<ElderScreen>,
    onTabSelected: (ElderScreen) -> Unit,
    currentScreen: ElderScreen
) {
    BottomNavigation(
        elevation = 4.dp,
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