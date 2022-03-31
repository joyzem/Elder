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
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.screens.report.ReportViewModelFactory
import com.example.elder.ui.theme.ElderTheme

class MainActivity : ComponentActivity() {

    val reportViewModel by viewModels<ReportViewModel> {
        ReportViewModelFactory((application as ElderApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderApp(reportViewModel = reportViewModel, onSendReport = this::sendReport)
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
fun ElderApp(reportViewModel: ReportViewModel, onSendReport: (GroupReport) -> Unit) {
    ElderTheme {
        var currentScreen by remember { mutableStateOf(ElderScreen.Report) }
        BackdropScaffold(
            appBar = {
                ElderTabRow(
                    ElderScreen.values().toList(),
                    currentScreen = currentScreen,
                    onTabSelected = {
                        currentScreen =
                            if (currentScreen == ElderScreen.Report) ElderScreen.Manage else ElderScreen.Report
                    }
                )
            },
            backLayerContent = {
                BackLayer(
                    modifier = Modifier.padding(16.dp),
                    date = reportViewModel.date,
                    onDateChange = reportViewModel::onDateChanged,
                    lesson = reportViewModel.lesson,
                    onLessonChange = reportViewModel::onLessonChanged,
                    selectMode = reportViewModel.selectMode,
                    onSelectModeChanged = reportViewModel::onSelectModeChanged
                )
            },
            frontLayerContent = {
                FrontLayer(
                    reportViewModel = reportViewModel,
                    onSendClicked = { onSendReport(reportViewModel.getReport()) }
                )
            },
            frontLayerElevation = 8.dp
        )
    }
}
