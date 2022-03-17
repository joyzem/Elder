package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import com.example.elder.ui.model.GroupReport
import com.example.elder.ui.theme.ElderTheme

class MainActivity : ComponentActivity() {

    val studentsViewModel by viewModels<StudentsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    StudentsScreen(
                        studentsViewModel = studentsViewModel
                    )
                }
            }
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
