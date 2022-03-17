package com.example.elder.ui.screens.parsing

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.elder.R
import com.example.elder.ui.screens.manage.ManageViewModel

@Composable
fun AutoFillDialogScreen(
    groupName: String,
    manageViewModel: ManageViewModel,
    onDismissRequest: () -> Unit,
    onSuccessAdded: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "alert"
    ) {
        composable("alert") {
            AskToFillDialog(
                onDismissRequest = onDismissRequest,
                manageViewModel = manageViewModel,
                navController = navController,
                groupName = groupName
            )
        }
        composable("parsing") {
            when (manageViewModel.parsingStatus) {
                is StudentsParsingStatus.Loading -> {
                    GroupLoadingScreen(
                        modifier = Modifier.padding(
                            vertical = 56.dp,
                            horizontal = 16.dp
                        ),
                        onDismissRequest = onDismissRequest
                    )
                }
                is StudentsParsingStatus.Success -> {
                    val students =
                        (manageViewModel.parsingStatus as StudentsParsingStatus.Success).result
                    GroupIsLoadedDialog(
                        onDismissRequest = onDismissRequest,
                        onConfirmButton = {
                            manageViewModel.insertGroup(students)
                            onSuccessAdded()
                        },
                        students = students.take(4)
                    )
                }
                is StudentsParsingStatus.Error -> {
                    ErrorDialog(
                        error = manageViewModel.parsingStatus as StudentsParsingStatus.Error,
                        onDismissRequest = onDismissRequest
                    )
                }
                is StudentsParsingStatus.Waiting -> {
                    return@composable
                }
            }
        }
    }
}

@Composable
private fun AskToFillDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    manageViewModel: ManageViewModel,
    navController: NavHostController,
    groupName: String
) {
    AlertDialog(
        title = { Text("Автозаполнение") },
        onDismissRequest = onDismissRequest,
        text = {
            Text(
                "Приложение может попробовать заполнить список твоей группы " +
                        "автоматически, взяв его из портфолио группы " +
                        "на сайте КубГАУ."
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    manageViewModel.setNewParsingStatus(StudentsParsingStatus.Loading)
                    navController.navigate("parsing")
                    manageViewModel.parseGroupByInternet(groupName)
                }
            ) {
                Text(text = "Продолжить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Заполнить самостоятельно")
            }
        },
        modifier = modifier
    )
}

@Composable
fun GroupLoadingScreen(modifier: Modifier = Modifier, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 300.dp)
        ) {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Пожалуйста, подождите. Совсем скоро ваша группа будет перед вами!",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun GroupIsLoadedDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onConfirmButton: () -> Unit,
    students: List<String>
) {
    AlertDialog(
        title = { Text("Это ваши студенты?") },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Нет, заполнить вручную")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmButton) {
                Text("Да, мои")
            }
        },
        text = {
            LazyColumn {
                items(students) { student ->
                    Text(text = student, modifier = Modifier.padding(vertical = 8.dp))
                }
                item {
                    Text("...")
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun ErrorDialog(
    modifier: Modifier = Modifier,
    error: StudentsParsingStatus.Error,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 400.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cloud_error),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Произошла ошибка\nКод ошибки: " +
                            if (error.error.containsKey("code"))
                                "группа не найдена.\nПроверьте написание группы, чтобы между " +
                                        "направлением и номером не было знаков" else "${error.error}",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismissRequest) {
                    Text("Закрыть")
                }
            }
        }
    }
}