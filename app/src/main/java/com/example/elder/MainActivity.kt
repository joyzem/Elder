package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.unit.dp
import com.example.elder.domain.ElderScreen
import com.example.elder.domain.GroupReport
import com.example.elder.ui.components.ElderTabRow
import com.example.elder.ui.screens.manage.ManageBackLayer
import com.example.elder.ui.screens.manage.ManageFrontLayer
import com.example.elder.ui.screens.manage.ManageViewModel
import com.example.elder.ui.screens.manage.ManageViewModelFactory
import com.example.elder.ui.screens.report.ReportBackLayer
import com.example.elder.ui.screens.report.ReportFrontLayer
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.screens.report.ReportViewModelFactory
import com.example.elder.ui.theme.DarkThemeColors
import com.example.elder.ui.theme.ElderTheme
import com.example.elder.ui.theme.LightThemeColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
            .putExtra(Intent.EXTRA_TEXT, report.content)
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
        BackdropScaffold(
            scaffoldState = backdropScaffoldState,
            appBar = {
                ElderTabRow(
                    ElderScreen.values().toList(),
                    currentScreen = currentScreen,
                    onTabSelected = {
                        currentScreen = it
                    }
                )
            },
            backLayerContent = {
                when (currentScreen) {
                    ElderScreen.Report -> {
                        ReportBackLayer(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                            reportViewModel = reportViewModel,
                            selectMode = reportViewModel.selectMode,
                            onSelectModeChanged = reportViewModel::onSelectModeChanged
                        )
                    }
                    ElderScreen.Manage -> {
                        ManageBackLayer(
                            manageViewModel = manageViewModel,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 8.dp,
                                top = 8.dp,
                                bottom = 16.dp
                            )
                        )
                    }
                }
            },
            frontLayerContent = {
                when (currentScreen) {
                    ElderScreen.Report -> {
                        ReportFrontLayer(
                            reportViewModel = reportViewModel,
                            onSendClicked = { onSendReport(reportViewModel.getReport()) }
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
            modifier = Modifier.pointerInput(Unit) {
                this.detectHorizontalDragGestures { change, dragAmount ->
                    val offset = Offset(x = dragAmount, y = 0f)
                    val newValue = Offset(offset.x.coerceIn(-200f, 200f), y = 0f)
                    if (newValue.x >= 55) {
                        currentScreen = ElderScreen.Report
                        return@detectHorizontalDragGestures
                    } else if (newValue.x <= -55){
                        currentScreen = ElderScreen.Manage
                        return@detectHorizontalDragGestures
                    }
                    change.consumePositionChange()
                }
            }
        )
    }
}
