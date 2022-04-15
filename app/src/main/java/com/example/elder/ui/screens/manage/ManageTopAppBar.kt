package com.example.elder.ui.screens.manage

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageTopAppBar(
    manageViewModel: ManageViewModel,
    onMenuClicked: () -> Unit,
    onAddPersonClicked: () -> Unit,
    backdropScaffoldState: BackdropScaffoldState  // When frontLayer is active add student is still active
) {
    TopAppBar(
        title = {
            Text(text = manageViewModel.groupName ?: "Задайте номер группы")
        },
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Меню"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onAddPersonClicked,
                modifier = Modifier.padding(end = 8.dp),
                enabled = backdropScaffoldState.isConcealed
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Добавить студента",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp
    )
}