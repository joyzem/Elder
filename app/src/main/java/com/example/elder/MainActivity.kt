package com.example.elder

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elder.domain.ElderScreen
import com.example.elder.domain.GroupReport
import com.example.elder.domain.SwipeDirection
import com.example.elder.ui.components.ElderBottomBar
import com.example.elder.ui.screens.manage.*
import com.example.elder.ui.screens.report.*
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
        val systemUiController = rememberSystemUiController()
        val isDarkTheme = isSystemInDarkTheme()
        SideEffect {
            systemUiController.setStatusBarColor(
                color = if (!isDarkTheme) LightThemeColors.background else DarkThemeColors.surface,
                darkIcons = !isDarkTheme
            )
        }
        val navController = rememberNavController().also {
            it.enableOnBackPressed(false)
        }
        Scaffold(
            bottomBar = {
                ElderBottomBar(
                    ElderScreen.values().toList(),
                    currentScreen = currentScreen,
                    onTabSelected = { screen ->
                        if (currentScreen == screen) return@ElderBottomBar
                        currentScreen = screen
                        navController.navigate(screen.name)
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = ElderScreen.Report.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(ElderScreen.Report.name) {
                    ReportScreen(
                        reportViewModel = reportViewModel,
                        groupName = manageViewModel.groupName ?: "Задайте номер группы",
                        onSendReport = onSendReport,
                        onSwipe = { swipeDirection ->
                            if (swipeDirection == SwipeDirection.LEFT) {
                                currentScreen = ElderScreen.Manage
                                navController.navigate(ElderScreen.Manage.name)
                            }
                        }
                    )
                }
                composable(ElderScreen.Manage.name) {
                    ManageScreen(
                        manageViewModel = manageViewModel,
                        onSwipe = { swipeDirection ->
                            if (swipeDirection == SwipeDirection.RIGHT) {
                                currentScreen = ElderScreen.Report
                                navController.navigate(ElderScreen.Report.name)
                            }
                        }
                    )
                }
            }
        }
    }
}