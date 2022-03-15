package com.example.elder

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.elder.ui.model.Lesson
import com.example.elder.ui.model.Student
import com.example.elder.ui.theme.ElderTheme
import java.text.DateFormat
import java.util.*

@Composable
fun StudentsScreen(
    students: List<Student>,
    onStudentChecked: (Student) -> Unit,
    date: Calendar,
    onDateChange: (Calendar) -> Unit,
    lesson: Lesson,
    onLessonChange: (Lesson) -> Unit,
    onAttendingStudentsClicked: () -> Unit,
    onMissingStudentsClicked: () -> Unit,
    onSelectAllCLick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            StudentsAppBar(
                date = date,
                context = context,
                lesson = lesson,
                onDateChange = onDateChange,
                onLessonChange = onLessonChange,
                onSelectAllCLick = onSelectAllCLick
            )
        }
    ) {
        Column {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = students) { student ->
                    StudentRow(student = student, onStudentChecked = { onStudentChecked(student) })
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
fun StudentRow(student: Student, onStudentChecked: (Student) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onStudentChecked(student)
            }
            .padding(16.dp)
    ) {
        Checkbox(
            checked = student.checked.value, onCheckedChange = {
                onStudentChecked(student)
            }
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(student.name)
    }
}

@Composable
fun StudentsAppBar(
    modifier: Modifier = Modifier,
    context: Context,
    date: Calendar,
    lesson: Lesson,
    onDateChange: (Calendar) -> Unit,
    onLessonChange: (Lesson) -> Unit,
    onSelectAllCLick: () -> Unit
) {
    val dateFormat: DateFormat = DateFormat.getDateInstance()
    var ifShowDialog by remember { mutableStateOf(false) }
    TopAppBar() {
        PickDateLabel(modifier, context, onDateChange, date, dateFormat)
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
    context: Context,
    onDateChange: (Calendar) -> Unit,
    date: Calendar,
    dateFormat: DateFormat
) {
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
    IconButton(
        onClick = onShowDialogButton,
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
    StudentsAppBar(
        date = Calendar.getInstance(),
        lesson = Lesson.FIRST,
        onDateChange = {},
        onLessonChange = {},
        context = LocalContext.current,
        onSelectAllCLick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    ElderTheme() {
        StudentsScreen(
            listOf(Student("Болотов")),
            {},
            Calendar.getInstance(),
            {},
            Lesson.FIRST,
            {},
            {},
            {},
            {}
        )
    }

}