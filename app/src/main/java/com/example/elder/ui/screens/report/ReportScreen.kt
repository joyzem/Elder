package com.example.elder

import android.app.DatePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import com.example.elder.domain.Lesson
import com.example.elder.ui.screens.report.StudentUiState
import com.example.elder.ui.screens.report.ReportViewModel
import com.example.elder.ui.screens.report.SelectMode
import com.example.elder.ui.theme.ElderTheme
import java.text.DateFormat
import java.util.*

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
private fun PickDateLabel(
    modifier: Modifier = Modifier,
    onDateChange: (Calendar) -> Unit,
    date: Calendar
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
            .border(
                0.5.dp,
                Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            )
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
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = DateFormat.getDateInstance().format(date.time),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar"
            )
        }
    }
}

@Composable
private fun PickLessonLabel(
    modifier: Modifier = Modifier,
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
            .border(
                0.5.dp,
                Color.Black.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            )
            .clickable {
                onShowDialogButton()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = lesson.value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = "Calendar"
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

@Composable
fun BackLayer(
    modifier: Modifier = Modifier,
    date: Calendar,
    onDateChange: (Calendar) -> Unit,
    lesson: Lesson,
    onLessonChange: (Lesson) -> Unit,
    selectMode: SelectMode,
    onSelectModeChanged: (SelectMode) -> Unit
) {
    Surface(modifier = modifier) {
        Column {
            var showDialog by remember { mutableStateOf(false) }
            PickDateLabel(onDateChange = onDateChange, date = date)
            Spacer(Modifier.height(16.dp))
            PickLessonLabel(
                showDialog = showDialog,
                lesson = lesson,
                onShowDialogButton = { showDialog = !showDialog },
                onLessonChange = onLessonChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            SelectModeRadioGroup(selectMode = selectMode, onSelectModeChanged = onSelectModeChanged)
        }
    }
}

@Composable
fun SelectModeRadioGroup(
    modifier: Modifier = Modifier,
    selectMode: SelectMode,
    onSelectModeChanged: (SelectMode) -> Unit
) {
    Row(modifier = modifier.selectableGroup()) {
        Row(
            Modifier
                .selectable(
                    selected = selectMode == SelectMode.AttendingStudents,
                    onClick = { onSelectModeChanged(SelectMode.AttendingStudents) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = selectMode == SelectMode.AttendingStudents,
                onClick = null
            )
            Text(text = "Присутствующие", modifier = Modifier.align(Alignment.CenterVertically))
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            Modifier
                .selectable(
                    selected = selectMode == SelectMode.MissingStudents,
                    onClick = { onSelectModeChanged(SelectMode.MissingStudents) },
                    role = Role.RadioButton
                )
                .padding(vertical = 8.dp)
        ) {
            RadioButton(
                selected = selectMode == SelectMode.MissingStudents,
                onClick = null
            )
            Text(text = "Отсутствующие", modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}

@Composable
fun FrontLayer(
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel,
    onSendClicked: () -> Unit
) {
    val students = reportViewModel.students
    Scaffold(modifier = modifier) { innerPadding ->
        Column {
            ListHeader(
                modifier = Modifier.padding(start = 16.dp),
                reportHeader = reportViewModel.reportHeader,
                onUnselectAllClicked = reportViewModel::checkAllStudents,
                onSelectAllClicked = reportViewModel::checkAllStudents,
                onSendClicked = onSendClicked
            )
            Divider(Modifier.height(1.dp))
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
        }
    }
}

@Composable
fun ListHeader(
    modifier: Modifier = Modifier,
    reportHeader: String,
    onUnselectAllClicked: (Boolean) -> Unit,
    onSelectAllClicked: (Boolean) -> Unit,
    onSendClicked: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = reportHeader)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onUnselectAllClicked(false) }) {
            Icon(
                painter = painterResource(id = R.drawable.unselect_all),
                contentDescription = "Unselect all"
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { onSelectAllClicked(true) }) {
            Icon(
                painter = painterResource(id = R.drawable.select_all),
                contentDescription = "Select all"
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSendClicked) {
            Icon(painter = painterResource(id = R.drawable.send), contentDescription = "Send")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BackLayerPreview() {
    ElderTheme {
        BackLayer(
            modifier = Modifier.padding(16.dp),
            date = Calendar.getInstance(),
            onDateChange = { },
            onLessonChange = { },
            lesson = Lesson.FIRST,
            selectMode = SelectMode.AttendingStudents,
            onSelectModeChanged = {}
        )
    }
}

@Preview
@Composable
fun FrontLayerPreview() {
    ElderTheme {
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