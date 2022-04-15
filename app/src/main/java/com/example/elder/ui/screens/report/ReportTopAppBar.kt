package com.example.elder.ui.screens.report

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportTopAppBar(
    reportViewModel: ReportViewModel,
    onSendClicked: () -> Unit,
    onMenuClicked: () -> Unit
) {
    val dateFormat = SimpleDateFormat("d MMM", Locale.getDefault())
    TopAppBar(
        title = {
            Text(text = "${dateFormat.format(reportViewModel.date.time)} ${reportViewModel.lesson.value}")
        },
        navigationIcon = {
            IconButton(onClick = { onMenuClicked() }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        actions = {
            TriStateCheckbox(
                state = reportViewModel.toggleableState,
                onClick = reportViewModel::onToggleableStateClicked,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary,
                    uncheckedColor = MaterialTheme.colors.onSurface
                )
            )
            IconButton(onClick = {
                onSendClicked()
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        },
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 0.dp
    )

}