package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.elder.domain.ElderScreen
import com.example.elder.domain.GroupReport
import com.example.elder.ui.components.ElderBottomBar
import com.example.elder.ui.screens.manage.*
import com.example.elder.ui.screens.report.*
import com.example.elder.ui.theme.DarkThemeColors
import com.example.elder.ui.theme.ElderTheme
import com.example.elder.ui.theme.LightThemeColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var groupName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupName = try {
            applicationContext.openFileInput(GROUP_NAME).bufferedReader().readLine()
        } catch (e: Exception) {
            null
        }
        val reportViewModel by viewModels<ReportViewModel> {
            ReportViewModelFactory(
                application = (application as ElderApplication),
                studentRepository = (application as ElderApplication).repository
            )
        }
        val manageViewModel by viewModels<ManageViewModel> {
            ManageViewModelFactory(
                studentRepository = (application as ElderApplication).repository,
                groupName = groupName
            )
        }
        setContent {
            ElderApp(
                reportViewModel = reportViewModel,
                manageViewModel = manageViewModel,
                onSendReport = this::sendReport
            )
        }
    }

    private fun sendReport(report: GroupReport) {
        val intent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, report.subject)
            .putExtra(Intent.EXTRA_TEXT, "${report.subject}\n${report.content}")
        if (packageManager.resolveActivity(intent, 0) != null) {
            startActivity(intent)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ElderApp(
    reportViewModel: ReportViewModel,
    manageViewModel: ManageViewModel,
    onSendReport: (GroupReport) -> Unit
) {
    ElderTheme {

        var currentScreen by rememberSaveable { mutableStateOf(ElderScreen.Report) }
        val backdropScaffoldState =
            rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)
        val systemUiController = rememberSystemUiController()
        val isDarkTheme = isSystemInDarkTheme()

        SideEffect {
            systemUiController.setStatusBarColor(
                color = if (!isDarkTheme) LightThemeColors.background else DarkThemeColors.surface,
                darkIcons = !isDarkTheme
            )
        }

        Scaffold(
            bottomBar = {
                ElderBottomBar(
                    ElderScreen.values().toList(),
                    currentScreen = currentScreen,
                    onTabSelected = {
                        currentScreen = it
                    }
                )
            }
        ) { innerPadding ->
            var showSendDialog by rememberSaveable {
                mutableStateOf(false)
            }
            if (showSendDialog) {
                SendDialog(
                    reportViewModel = reportViewModel,
                    onDismissRequest = { showSendDialog = false },
                    onSendReport = onSendReport
                )
            }
            val scope = rememberCoroutineScope()
            BackdropScaffold(
                scaffoldState = backdropScaffoldState,
                appBar = {
                    when (currentScreen) {
                        ElderScreen.Report -> {
                            ReportTopAppBar(
                                reportViewModel,
                                onSendClicked = {
                                    showSendDialog = true
                                },
                                onMenuClicked = {
                                    scope.launch {
                                        backdropScaffoldState.reveal()
                                    }
                                }
                            )
                        }
                        ElderScreen.Manage -> {
                            ManageTopAppBar(
                                manageViewModel = manageViewModel,
                                onMenuClicked = {
                                    scope.launch {
                                        backdropScaffoldState.reveal()
                                    }
                                },
                                onAddPersonClicked = { }
                            )
                        }
                    }
                },
                backLayerContent = {
                    when (currentScreen) {
                        ElderScreen.Report -> {
                            val groupName = manageViewModel.groupName ?: "Название группы"
                            ReportBackLayer(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                                reportViewModel = reportViewModel,
                                onSendClicked = { showSendDialog = true },
                                groupName = groupName
                            )
                        }
                        ElderScreen.Manage -> {
                            ManageBackLayer(
                                manageViewModel = manageViewModel,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 16.dp
                                )
                            )
                        }
                    }
                },
                persistentAppBar = false,
                frontLayerContent = {
                    when (currentScreen) {
                        ElderScreen.Report -> {
                            ReportFrontLayer(
                                reportViewModel = reportViewModel
                            )
                        }
                        ElderScreen.Manage -> {
                            ManageFrontLayer(manageViewModel = manageViewModel)
                        }
                    }
                },
                gesturesEnabled = true,
                frontLayerElevation = 8.dp,
                backLayerBackgroundColor = MaterialTheme.colors.surface,
                modifier = Modifier
                    .padding(innerPadding)
                    .pointerInput(Unit) {
                        this.detectHorizontalDragGestures { change, dragAmount ->
                            val offset = Offset(x = dragAmount, y = 0f)
                            val newValue = Offset(offset.x.coerceIn(-200f, 200f), y = 0f)
                            if (newValue.x >= 55) {
                                currentScreen = ElderScreen.Report
                                return@detectHorizontalDragGestures
                            } else if (newValue.x <= -55) {
                                currentScreen = ElderScreen.Manage
                                return@detectHorizontalDragGestures
                            }
                            change.consumePositionChange()
                        }
                    }
            )

        }
    }
}

@Composable
fun SendDialog(
    reportViewModel: ReportViewModel,
    onDismissRequest: () -> Unit,
    onSendReport: (GroupReport) -> Unit
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "Выберите фильтр", Modifier.padding(16.dp))
                Divider(Modifier.height(1.dp))
                Text(
                    text = "Присутствующие",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.AttendingStudents)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Text(
                    text = "Отсутствующие",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.MissingStudents)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Text(
                    text = "Все",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.Both)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
