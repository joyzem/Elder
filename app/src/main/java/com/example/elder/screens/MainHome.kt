package com.example.elder.screens

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat.startActivity
import com.example.elder.ElderApplication
import com.example.elder.ElderScreen
import com.example.elder.MainActivity
import com.example.elder.ReportScreen
import com.example.elder.data.students.StudentRepository
import com.example.elder.domain.GroupReport
import com.example.elder.screens.report.CreateReportViewModelFactory
import com.example.elder.screens.report.ReportViewModel

@Composable
fun MainHome(
    studentsRep: StudentRepository,
    activity: MainActivity
) {
    val createReportViewModel by activity.viewModels<ReportViewModel> {
        CreateReportViewModelFactory(studentsRep)
    }

    var tabSelected by remember { mutableStateOf(ElderScreen.Manage)}


    ReportScreen(
        reportViewModel = createReportViewModel,
        onAttendingStudentsClicked = {
            sendReport(createReportViewModel.onAttendingStudentsRequest(), activity.packageManager, activity.applicationContext)
        },
        onMissingStudentsClicked = {
            sendReport(createReportViewModel.onMissingStudentsRequest(), activity.packageManager, activity.applicationContext)
        }
    )
}

private fun sendReport(report: GroupReport, packageManager: PackageManager, context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, report.subject)
        .putExtra(Intent.EXTRA_TEXT, report.content)
    if (packageManager.resolveActivity(intent, 0) != null) {
        startActivity(context, intent, null)
    }
}
