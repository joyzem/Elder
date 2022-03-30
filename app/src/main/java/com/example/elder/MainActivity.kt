package com.example.elder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import com.example.elder.ui.theme.ElderTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderApp()
        }
    }
}

@Composable
fun ElderApp() {
    ElderTheme {
        Scaffold(

        ) {
        }
    }
}
