package com.example.elder.ui.screens.manage

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.elder.R
import com.example.elder.data.students.Student
import com.example.elder.ui.theme.ElderTheme

@Composable
fun ManageFrontLayer(
    modifier: Modifier = Modifier,
    manageViewModel: ManageViewModel
) {
    val students = manageViewModel.students
    val groupName = manageViewModel.groupName
    var showStudentAddDialog by remember { mutableStateOf(false) }
    Scaffold(modifier = modifier) {
        if (showStudentAddDialog) {
            AddStudentDialog(
                onDismissRequest = { showStudentAddDialog = false },
                onAddButtonClicked = manageViewModel::insertStudent
            )
        }
        Column {
            ManageListHeader(
                modifier = Modifier.padding(start = 16.dp, end = 10.dp),
                groupName,
                onAddPersonClicked = { showStudentAddDialog = true })
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
private fun ManageListHeader(
    modifier: Modifier = Modifier,
    groupName: String?,
    onAddPersonClicked: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = groupName ?: "Задайте номер группы"
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onAddPersonClicked
        ) {
            Icon(
                painter = painterResource(id = R.drawable.person_add),
                contentDescription = "Добавить студента"
            )
        }
    }
}

@Composable
fun AddStudentDialog(onDismissRequest: () -> Unit, onAddButtonClicked: (String) -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        var studentName by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = studentName,
                    onValueChange = {
                        studentName = it
                        isError = false
                    },
                    label = { Text("Фамилия студента") },
                    isError = isError,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = MaterialTheme.colors.onSurface,
                        cursorColor = MaterialTheme.colors.onSurface,
                        focusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                        focusedLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (studentName == "") {
                            isError = true
                        } else {
                            onAddButtonClicked(studentName)
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

@Composable
fun ManageBackLayer(modifier: Modifier = Modifier, manageViewModel: ManageViewModel) {
    Row(modifier) {
        var groupName by remember { mutableStateOf(manageViewModel.groupName ?: "") }
        val context = LocalContext.current
        var isInputIncorrect by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = groupName,
            onValueChange = {
                groupName = it;
                isInputIncorrect = false
            },
            label = { Text(text = "Номер группы") },
            modifier = Modifier.weight(1f),
            isError = isInputIncorrect,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.onSurface,
                cursorColor = MaterialTheme.colors.onSurface,
                focusedBorderColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
            )
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            onClick = {
                if (groupName.isEmpty()) {
                    isInputIncorrect = true
                    Toast.makeText(context, "Заполните поле", Toast.LENGTH_SHORT).show()
                } else {
                    manageViewModel.saveGroupName(
                        groupName = groupName,
                        context = context
                    )
                }
            },
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(imageVector = Icons.Filled.Done, contentDescription = "Submit")
        }
    }
}

@Composable
fun ManageableStudentRow(
    modifier: Modifier = Modifier,
    student: Student,
    onDeleteButtonClick: (Student) -> Unit
) {
    Row(
        modifier = modifier
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = student.surname)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = { onDeleteButtonClick(student) },
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = null)
        }
    }
}

@Preview
@Composable
fun ManageableStudentRowPreview() {
    ElderTheme {
        ManageableStudentRow(
            student = Student(surname = "Surname"),
            onDeleteButtonClick = {})
    }
}