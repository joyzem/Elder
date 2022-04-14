package com.example.elder.ui.screens.manage

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.elder.R
import com.example.elder.data.students.Student
import com.example.elder.domain.SwipeDirection
import com.example.elder.ui.components.ElderOutlinedButton
import com.example.elder.ui.screens.parsing.AutoFillDialogScreen
import com.example.elder.ui.screens.parsing.StudentsParsingStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManageScreen(
    manageViewModel: ManageViewModel,
    onSwipe: (SwipeDirection) -> Unit
) {
    val backdropScaffoldState =
        rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)
    var showStudentAddDialog by rememberSaveable { mutableStateOf(false) }
    if (showStudentAddDialog) {
        AddStudentDialog(
            onDismissRequest = { showStudentAddDialog = false },
            onAddButtonClicked = manageViewModel::insertStudent
        )
    }
    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        appBar = {
            val scope = rememberCoroutineScope()
            ManageTopAppBar(
                manageViewModel = manageViewModel,
                onMenuClicked = {
                    scope.launch {
                        backdropScaffoldState.reveal()
                    }
                },
                onAddPersonClicked = { showStudentAddDialog = true }
            )
        },
        backLayerContent = {
            ManageBackLayer(
                manageViewModel = manageViewModel,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
            )
        },
        frontLayerContent = {
            ManageFrontLayer(manageViewModel = manageViewModel)
        },
        persistentAppBar = false,
        frontLayerElevation = 8.dp,
        backLayerBackgroundColor = MaterialTheme.colors.surface,
        gesturesEnabled = true,
        modifier = Modifier.pointerInput(Unit) {
            this.detectHorizontalDragGestures { change, dragAmount ->
                val offset = Offset(x = dragAmount, y = 0f)
                val newValue = Offset(offset.x.coerceIn(-200f, 200f), y = 0f)
                if (newValue.x >= 55) {
                    onSwipe(SwipeDirection.RIGHT)
                    return@detectHorizontalDragGestures
                } else if (newValue.x <= -55) {
                    onSwipe(SwipeDirection.LEFT)
                    return@detectHorizontalDragGestures
                }
                change.consumePositionChange()
            }
        }
    )
}

@Composable
private fun ManageFrontLayer(
    modifier: Modifier = Modifier,
    manageViewModel: ManageViewModel
) {
    val students = manageViewModel.students
    Scaffold(modifier = modifier) {
        if (students.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Нажмите, чтобы войти в режим добавления",
                    Modifier.padding(32.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        LazyColumn {
            items(students) { student ->
                StudentRow(
                    modifier = Modifier.padding(start = 16.dp),
                    student = student,
                    onDeleteButtonClick = manageViewModel::deleteStudent
                )
            }
        }
    }
}

@Composable
private fun ManageBackLayer(modifier: Modifier = Modifier, manageViewModel: ManageViewModel) {
    var groupNumber by remember { mutableStateOf(manageViewModel.groupName ?: "") }
    val context = LocalContext.current
    var ifShowAutoFillingDialog by rememberSaveable { mutableStateOf(false) }
    if (ifShowAutoFillingDialog) {
        AutoFillDialogScreen(
            groupNumber,
            manageViewModel = manageViewModel,
            onDismissRequest = {
                ifShowAutoFillingDialog = false
                manageViewModel.setNewParsingStatus(StudentsParsingStatus.Waiting)
            },
            onSuccessAdded = {
                makeToast(context, "Группа добавлена!")
                ifShowAutoFillingDialog = false
                manageViewModel.setNewParsingStatus(StudentsParsingStatus.Waiting)
                groupNumber = groupNumber.toUpperCase(Locale.current)
                manageViewModel.saveGroupName(groupName = groupNumber, context)
            }
        )
    }
    var isInputIncorrect by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier) {
        GroupNumberInput(
            groupNumber = groupNumber,
            onGroupNumberChange = { number ->
                groupNumber = number
                isInputIncorrect = false
            },
            onSubmitIconClick = {
                if (groupNumber.isEmpty()) {
                    isInputIncorrect = true
                } else {
                    groupNumber = groupNumber.toUpperCase(Locale.current)
                    manageViewModel.saveGroupName(groupNumber, context)
                    makeToast(context, "Сохранено!")
                }
            },
            isInputIncorrect = isInputIncorrect
        )
        if (isInputIncorrect) {
            Text(
                text = "Заполните поле",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        val focusManager = LocalFocusManager.current
        ElderOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                focusManager.clearFocus()
                if (groupNumber.isEmpty()) {
                    isInputIncorrect = true
                } else {
                    ifShowAutoFillingDialog = true
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Outlined.Error, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Заполнить автоматически")
            }
        }
    }
}

@Composable
private fun GroupNumberInput(
    groupNumber: String,
    onGroupNumberChange: (String) -> Unit,
    onSubmitIconClick: () -> Unit,
    isInputIncorrect: Boolean
) {
    Row {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = groupNumber,
            onValueChange = onGroupNumberChange,
            label = { Text(text = "Номер группы") },
            placeholder = { Text(text = "ПИ2002") },
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onSubmitIconClick()
            }),
            modifier = Modifier.weight(1f),
            isError = isInputIncorrect
        )
        IconButton(
            onClick = onSubmitIconClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.done),
                contentDescription = "Подтвердить"
            )
        }
    }
}

@Composable
private fun AddStudentDialog(
    onDismissRequest: () -> Unit,
    onAddButtonClicked: (String) -> Unit
) {
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
                val focusManager = LocalFocusManager.current
                OutlinedTextField(
                    shape = MaterialTheme.shapes.small,
                    value = studentName,
                    onValueChange = {
                        studentName = it
                        isError = false
                    },
                    label = { Text("Фамилия студента") },
                    isError = isError,
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ElderOutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        if (studentName.isEmpty()) {
                            isError = true
                        } else {
                            onAddButtonClicked(studentName)
                            makeToast(context, "$studentName добавлен(а)")
                            studentName = ""
                            focusManager.clearFocus()
                        }
                    }
                ) {
                    Text(text = "Добавить")
                }
            }
        }
    }
}

private fun makeToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


@Composable
private fun StudentRow(
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