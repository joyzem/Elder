package com.example.elder.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.elder.R
import com.example.elder.ui.theme.ElderTheme

@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel()) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(bottom = 200.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
            )
            Text(
                text = "Ваша группа",
                modifier = Modifier.padding(top = (0).dp, bottom = 16.dp),
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.h5
            )
            OutlinedTextField(
                value = viewModel.groupName,
                onValueChange = viewModel::onGroupNameChange,
                placeholder = {
                    Text("Введите номер")
                }
            )
            TextButton(
                onClick = { viewModel.getGroupList() },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Продолжить".toUpperCase(Locale.current))
            }
        }
    }
}


@Preview()
@Composable
fun PreviewAuthScreen() {
    val viewModel: AuthViewModel = viewModel()
    ElderTheme(useDarkTheme = false) {
        AuthScreen(viewModel)
    }
}