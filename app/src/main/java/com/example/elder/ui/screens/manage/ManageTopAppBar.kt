package com.example.elder.ui.screens.manage

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun ManageTopAppBar(
    manageViewModel: ManageViewModel,
    onMenuClicked: () -> Unit,
    onAddPersonClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = manageViewModel.groupName ?: "Задайте номер группы")
        },
        navigationIcon = {
            IconButton(onClick = onMenuClicked){
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Меню"
                )
            }
        },
        actions = {
            IconButton(onClick = onAddPersonClicked) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Добавить студента"
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp
    )
}