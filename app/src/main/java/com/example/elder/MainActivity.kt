package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elder.base.ElderTabRow
import com.example.elder.domain.GroupReport
import com.example.elder.ui.screens.manage.ManageBackLayer
import com.example.elder.ui.screens.manage.ManageFrontLayer
import com.example.elder.ui.screens.manage.ManageViewModel
import com.example.elder.ui.screens.manage.ManageViewModelFactory
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.screens.report.ReportViewModelFactory
import com.example.elder.ui.theme.ElderTheme

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
        var currentScreen by remember { mutableStateOf(ElderScreen.Report) }
        BackdropScaffold(
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
                            modifier = Modifier.padding(16.dp),
                            date = reportViewModel.date,
                            onDateChange = reportViewModel::onDateChanged,
                            lesson = reportViewModel.lesson,
                            onLessonChange = reportViewModel::onLessonChanged,
                            selectMode = reportViewModel.selectMode,
                            onSelectModeChanged = reportViewModel::onSelectModeChanged
                        )
                    }
                    ElderScreen.Manage -> {
                        ManageBackLayer(
                            manageViewModel = manageViewModel, modifier = Modifier.padding(16.dp)
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
            frontLayerElevation = 8.dp,
            backLayerBackgroundColor = MaterialTheme.colors.surface
        )
    }
}
