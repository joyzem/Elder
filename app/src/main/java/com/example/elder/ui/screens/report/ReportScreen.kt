package com.example.elder

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Dialog
import com.example.elder.domain.Lesson
import com.example.elder.ui.screens.report.StudentUiState
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.theme.ElderTheme
import java.text.DateFormat
import java.util.*

@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel,
    onAttendingStudentsClicked: () -> Unit,
    onMissingStudentsClicked: () -> Unit
) {
    val students = reportViewModel.group
    Scaffold(
        topBar = {
            StudentsAppBar(
                date = reportViewModel.date,
                lesson = reportViewModel.lesson,
                onDateChange = reportViewModel::onDateChanged,
                onLessonChange = reportViewModel::onLessonChanged
            ) {
                reportViewModel.checkAllStudents(checked = true)
            }
        }
    ) {
        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = students) { student ->
                    StudentRow(
                        onStudentChecked = {
                            reportViewModel.onStudentChecked(student)
                        },
                        studentUiState = student
                    )
                }
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                color = Color.DarkGray
            )
            Footer(
                onAttendingStudentsClicked = onAttendingStudentsClicked,
                onMissingStudentsClicked = onMissingStudentsClicked,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun Footer(
    onAttendingStudentsClicked: () -> Unit,
    onMissingStudentsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        // Attending students
        OutlinedButton(
            onClick = onAttendingStudentsClicked,
            modifier = Modifier
                .weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface
            )
        ) {
            Text(
                text = "Присутствующие"
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        // Missing students
        OutlinedButton(
            onClick = onMissingStudentsClicked,
            modifier = Modifier
                .weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.onSurface
            )
        ) {
            Text(
                text = "Отсутствующие"
            )
        }
    }
}

@Composable
fun StudentRow(onStudentChecked: () -> Unit, studentUiState: StudentUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                studentUiState.checked, onValueChange = {
                    onStudentChecked()
                },
                role = Role.Checkbox
            )
            .padding(16.dp)
    ) {
        Checkbox(
            checked = studentUiState.checked, onCheckedChange = null
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(studentUiState.name)
    }
}

@Composable
fun StudentsAppBar(
    modifier: Modifier = Modifier,
    date: Calendar,
    lesson: Lesson,
    onDateChange: (Calendar) -> Unit,
    onLessonChange: (Lesson) -> Unit,
    onSelectAllCLick: () -> Unit
) {
    val dateFormat: DateFormat = DateFormat.getDateInstance()
    var ifShowDialog by remember { mutableStateOf(false) }
    TopAppBar {
        PickDateLabel(modifier, onDateChange, date, dateFormat)
        PickLessonLabel(
            showDialog = ifShowDialog,
            lesson = lesson,
            onShowDialogButton = { ifShowDialog = !ifShowDialog },
            onLessonChange = onLessonChange
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSelectAllCLick
        ) {
            Icon(
                painter = painterResource(id = R.drawable.select_all),
                contentDescription = "Select all"
            )
        }
    }
}

@Composable
private fun PickDateLabel(
    modifier: Modifier = Modifier,
    onDateChange: (Calendar) -> Unit,
    date: Calendar,
    dateFormat: DateFormat
) {
    val context = LocalContext.current
    Card(
        elevation = 0.dp,
        modifier = modifier
            .fillMaxHeight()
            .clickable {
                DatePickerDialog(
                    context,
                    { _, year, month, date ->
                        val newDate = Calendar.getInstance()
                        newDate.set(year, month, date)
                        onDateChange(newDate)
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DATE),
                ).show()
            },
        backgroundColor = Color.Transparent
    ) {
        Row(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar"
            )
            Spacer(Modifier.padding(4.dp))
            Text(
                text = dateFormat.format(date.time),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun PickLessonLabel(
    showDialog: Boolean,
    lesson: Lesson,
    onShowDialogButton: () -> Unit,
    onLessonChange: (Lesson) -> Unit
) {
    if (showDialog) {
        LessonDialog(
            onDismissRequest = onShowDialogButton,
            onLessonClicked = onLessonChange
        )
    }
    Card(
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxHeight()
            .clickable {
                onShowDialogButton()
            }
    ) {
        Row(modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 16.dp, end = 16.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.expand),
                contentDescription = "Choose lesson"
            )
            Text(
                text = lesson.value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun LessonDialog(
    onDismissRequest: () -> Unit,
    onLessonClicked: (Lesson) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
            elevation = 8.dp
        ) {
            Column {
                Lesson.values().forEach { lesson ->
                    Text(
                        text = lesson.value,
                        modifier = Modifier
                            .clickable {
                                onLessonClicked(lesson)
                                onDismissRequest()
                            }
                            .fillMaxWidth()
                            .padding(16.dp),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun StudentsAppBarPreview() {
    ElderTheme {
        StudentsAppBar(
            date = Calendar.getInstance(),
            lesson = Lesson.FIRST,
            onDateChange = {},
            onLessonChange = {}
        ) {}
    }
}

@Preview()
@Composable
fun HomePreview() {
    ElderTheme {
        Surface() {
            
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview()
@Composable
fun PreviewBackdrop() {
    ElderTheme {
        BackdropScaffold(
            appBar = { /*TODO*/ },
            backLayerContent = { /*TODO*/ },
            frontLayerContent = {}
        ) {

        }
    }
}