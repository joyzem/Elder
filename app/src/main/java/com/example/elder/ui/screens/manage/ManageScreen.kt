package com.example.elder.ui.screens.manage

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.elder.R
import com.example.elder.data.students.Student
import com.example.elder.ui.components.ElderOutlinedButton
import com.example.elder.ui.screens.parsing.AutoFillDialogScreen
import com.example.elder.ui.screens.parsing.StudentsParsingStatus

@Composable
fun ManageFrontLayer(
    modifier: Modifier = Modifier,
    manageViewModel: ManageViewModel
) {
    val students = manageViewModel.students
    val groupName = manageViewModel.groupName
    var showStudentAddDialog by rememberSaveable { mutableStateOf(false) }
    Scaffold(modifier = modifier) {
        AddStudentDialog(
            shouldShow = showStudentAddDialog,
            onDismissRequest = { showStudentAddDialog = false },
            onAddButtonClicked = manageViewModel::insertStudent
        )
        Column {
            ManageListHeader(
                modifier = Modifier.padding(start = 16.dp, end = 10.dp),
                groupName,
                onAddPersonClicked = { showStudentAddDialog = true }
            )
            Divider(Modifier.height(1.dp))
            LazyColumn {
                items(students) { student ->
                    ManageableStudentRow(
                        modifier = Modifier.padding(start = 16.dp),
                        student = student,
                        onDeleteButtonClick = manageViewModel::deleteStudent
                    )
                }
            }
        }
    }
}

@Composable
fun ManageBackLayer(modifier: Modifier = Modifier, manageViewModel: ManageViewModel) {
    var groupName by remember { mutableStateOf(manageViewModel.groupName ?: "") }
    val context = LocalContext.current
    var isInputIncorrect by rememberSaveable { mutableStateOf(false) }
    var ifShowAutoFillingDialog by rememberSaveable { mutableStateOf(false) }
    if (ifShowAutoFillingDialog) {
        AutoFillDialogScreen(
            groupName,
            manageViewModel = manageViewModel,
            onDismissRequest = {
                ifShowAutoFillingDialog = false
                manageViewModel.setNewParsingStatus(StudentsParsingStatus.Waiting)
            },
            onSuccessAdded = {
                makeToast(context, "Группа добавлена!")
                ifShowAutoFillingDialog = false
                manageViewModel.setNewParsingStatus(StudentsParsingStatus.Waiting)
                groupName = groupName.toUpperCase(Locale.current)
                manageViewModel.saveGroupName(groupName = groupName, context)
            }
        )
    }
    Column(modifier = modifier) {
        Row {
            OutlinedTextField(
                shape = CircleShape,
                value = groupName,
                onValueChange = {
                    groupName = it
                    isInputIncorrect = false
                },
                label = { Text(text = "Номер группы") },
                modifier = Modifier.weight(1f),
                isError = isInputIncorrect
            )
            IconButton(
                onClick = {
                    if (groupName.isEmpty()) {
                        isInputIncorrect = true
                    } else {
                        groupName = groupName.toUpperCase(Locale.current)
                        manageViewModel.saveGroupName(groupName, context)
                        makeToast(context, "Сохранено!")
                    }
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.done),
                    contentDescription = "Подтвердить"
                )
            }
        }
        if (isInputIncorrect) {
            Text(
                text = "Заполните поле",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        ElderOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (groupName.isEmpty()) {
                    isInputIncorrect = true
                } else {
                    ifShowAutoFillingDialog = true
                }
            }
        ) {
            Text(text = "Заполнить автоматически")
        }
    }
}

@Composable
private fun ManageListHeader(
    modifier: Modifier = Modifier,
    groupName: String?,
    onAddPersonClicked: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = groupName ?: "Задайте номер группы")
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onAddPersonClicked) {
            Icon(
                painter = painterResource(id = R.drawable.person_add),
                contentDescription = "Добавить студента"
            )
        }
    }
}

@Composable
fun AddStudentDialog(
    shouldShow: Boolean,
    onDismissRequest: () -> Unit,
    onAddButtonClicked: (String) -> Unit
) {
    if (shouldShow) {
        Dialog(onDismissRequest = onDismissRequest) {
            var studentName by rememberSaveable { mutableStateOf("") }
            var isError by rememberSaveable { mutableStateOf(false) }
            val context = LocalContext.current
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        shape = CircleShape,
                        value = studentName,
                        onValueChange = {
                            studentName = it
                            isError = false
                        },
                        label = { Text("Фамилия студента") },
                        isError = isError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ElderOutlinedButton(
                        modifier = Modifier,
                        onClick = {
                            if (studentName == "") {
                                isError = true
                            } else {
                                onAddButtonClicked(studentName)
                                makeToast(context, "$studentName добавлен(а)")
                                studentName = ""
                            }
                        }
                    ) {
                        Text(text = "Добавить")
                    }
                }
            }
        }
    }
}

private fun makeToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@Composable
fun ManageableStudentRow(
    modifier: Modifier = Modifier,
    student: Student,
    onDeleteButtonClick: (Student) -> Unit
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = student.surname)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onDeleteButtonClick(student) },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "Удалить"
            )
        }
    }
}