package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elder.domain.GroupReport
import com.example.elder.screens.SplashScreen
import com.example.elder.screens.select.CreateReportViewModelFactory
import com.example.elder.screens.select.CreateReportViewModel
import com.example.elder.ui.theme.ElderTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderTheme {
                val navController = rememberNavController()
                val systemUiController = rememberSystemUiController()

                Surface {
                    NavHost(navController = navController, startDestination = CREATE_REPORT_ROUTE) {
                        composable(SPLASH_ROUTE) {
                            SplashScreen(navController = navController)
                        }

                        composable(CREATE_REPORT_ROUTE) {
                            val createReportViewModel by viewModels<CreateReportViewModel> {
                                CreateReportViewModelFactory((application as ElderApplication).repository)
                            }
                            CreateReportScreen(
                                createReportViewModel = createReportViewModel,
                                onAttendingStudentsClicked = {
                                    sendReport(createReportViewModel.onAttendingStudentsRequest())
                                },
                                onMissingStudentsClicked = {
                                    sendReport(createReportViewModel.onMissingStudentsRequest())
                                }
                            )
                        }

                        composable(MANAGE_ROUTE) {

                        }
                    }
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
