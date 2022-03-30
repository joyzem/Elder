package com.example.elder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elder.base.ElderTabRow
import com.example.elder.screens.MainHome
import com.example.elder.screens.SplashScreen
import com.example.elder.ui.theme.ElderTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderTheme {
                Surface {
                    NavHost(navController = navController, startDestination = SPLASH_ROUTE) {
                        composable(SPLASH_ROUTE) {
                            SplashScreen(navController = navController)
                        }

                        composable(MAIN_SCREEN) {
                            MainHome(
                                activity = this@MainActivity,
                                studentsRep = (application as ElderApplication).repository
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ElderApp() {
    ElderTheme {
        val allScreens = ElderScreen.values().toList()
        val navController = rememberNavController()
        Scaffold(
            topBar = ElderTabRow(

            )
        ) {

        }
    }
}
