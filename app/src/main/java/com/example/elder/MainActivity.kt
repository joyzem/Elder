package com.example.elder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.elder.base.ElderTabRow
import com.example.elder.ui.theme.ElderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderApp()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ElderApp() {
    ElderTheme {
        BackdropScaffold(
            appBar = {
                ElderTabRow(
                    ElderScreen.values().toList(),
                    currentScreen = ElderScreen.Report,
                    onTabSelected = {  }
                )
            },
            backLayerContent = { Text("Back layer") },
            frontLayerContent = { Text("Front layer") }
        ) {

        }
    }
}
