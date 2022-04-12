package com.example.elder.ui.screens.report

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import com.example.elder.R
import com.example.elder.domain.Lesson
import com.example.elder.ui.components.ClickableCard
import java.text.DateFormat
import java.util.*

@Composable
fun ReportFrontLayer(
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel,
    onSendClicked: () -> Unit
) {
    val students = reportViewModel.students
    Scaffold(modifier = modifier) {
        Column {
            ListHeader(
                modifier = Modifier.padding(start = 16.dp),
                reportHeader = reportViewModel.reportHeader,
                onSelectAllClicked = { bool ->
                    reportViewModel.checkAllStudents(bool)
                },
                onSendClicked = onSendClicked
            )
            Divider(Modifier.height(1.dp))
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items = students) { student ->
                    StudentRow(
                        onStudentChecked = {
                            reportViewModel.onStudentChecked(student)
                        },
                        reportStudentUiState = student
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun ListHeader(
    modifier: Modifier = Modifier,
    reportHeader: String,
    onSelectAllClicked: (Boolean) -> Unit,
    onSendClicked: () -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = reportHeader)
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onSelectAllClicked(false) }) {
            Icon(
                painter = painterResource(id = R.drawable.uncheck_all),
                contentDescription = "Unselect all"
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { onSelectAllClicked(true) }) {
            Icon(
                painter = painterResource(id = R.drawable.check_all),
                contentDescription = "Select all"
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onSendClicked) {
            Icon(painter = painterResource(id = R.drawable.send), contentDescription = "Send")
        }
    }
}


/**
 * StudentRow is a view that shows student info and allows to change studentUiState
 * @param onStudentChecked Actions when user clicks the row
 * @param reportStudentUiState Contains data that is shown on screen
 */
@Composable
private fun StudentRow(
    onStudentChecked: (Boolean) -> Unit,
    reportStudentUiState: ReportStudentUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                reportStudentUiState.checked,
                onValueChange = { boolean ->
                    onStudentChecked(boolean)
                },
                role = Role.Button
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(visible = reportStudentUiState.checked) {
                Checkbox(
                    checked = reportStudentUiState.checked, onCheckedChange = null
                )
            }
            AnimatedVisibility(visible = reportStudentUiState.checked) {
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
            }
            Text(reportStudentUiState.student.surname)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onStudentChecked(!reportStudentUiState.checked) },
                shape = CircleShape,
                colors = if (reportStudentUiState.checked) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f)
                        .compositeOver(MaterialTheme.colors.surface),
                    contentColor = MaterialTheme.colors.onSurface
                ),
                modifier = Modifier.animateContentSize()
            ) {
                Text(text = if (reportStudentUiState.checked) "Присутствует" else "Отсутствует")
            }
        }
        AnimatedVisibility(visible = !reportStudentUiState.checked) {
            Spacer(Modifier.height(8.dp))
        }
        AnimatedVisibility(visible = !reportStudentUiState.checked) {
            OutlinedTextField(
                value = reportStudentUiState.reasonOfMissing.value,
                onValueChange = { reportStudentUiState.reasonOfMissing.value = it },
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                label = { Text("Причина отсутствия (необязательно)") },
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                singleLine = true
            )
        }
    }
}

@Composable
fun ReportBackLayer(
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel,
    selectMode: SelectMode,
    onSelectModeChanged: (SelectMode) -> Unit
) {
    Surface(modifier = modifier) {
        Column(Modifier.padding(horizontal = 4.dp)) {
            var showDialog by remember { mutableStateOf(false) }
            PickDateCard(
                onDateChange = reportViewModel::onDateChanged,
                date = reportViewModel.date
            )
            Spacer(Modifier.height(12.dp))
            PickLessonCard(
                showDialog = showDialog,
                lesson = reportViewModel.lesson,
                onShowDialogButton = { showDialog = !showDialog },
                onLessonChange = reportViewModel::onLessonChanged
            )
            SelectModeRadioGroup(
                modifier = Modifier.fillMaxWidth(),
                selectMode = selectMode,
                onSelectModeChanged = onSelectModeChanged
            )
        }
    }
}

@Composable
private fun PickDateCard(
    modifier: Modifier = Modifier,
    onDateChange: (Calendar) -> Unit,
    date: Calendar
) {
    val context = LocalContext.current
    ClickableCard(
        modifier = modifier,
        onClick = {
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
        TextSpaceIconRow(
            modifier = Modifier.padding(16.dp),
            text = DateFormat.getDateInstance().format(date.time),
            icon = painterResource(id = R.drawable.calendar),
            iconDescription = "Выбрать дату"
        )
    }
}

@Composable
private fun TextSpaceIconRow(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter,
    iconDescription: String
) {
    Row(
        modifier = modifier
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.weight(1f))
        Icon(painter = icon, contentDescription = iconDescription)
    }
}

@Composable
private fun PickLessonCard(
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
    ClickableCard(modifier = modifier, onClick = onShowDialogButton) {
        TextSpaceIconRow(
            modifier = Modifier.padding(16.dp),
            text = lesson.value,
            icon = painterResource(id = R.drawable.ic_time),
            iconDescription = "Выбрать пару"
        )
    }
}

@Composable
private fun LessonDialog(
    onDismissRequest: () -> Unit,
    onLessonClicked: (Lesson) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(8.dp)
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
private fun SelectModeRadioGroup(
    modifier: Modifier = Modifier,
    selectMode: SelectMode,
    onSelectModeChanged: (SelectMode) -> Unit
) {
    Row(modifier = modifier.selectableGroup(), horizontalArrangement = Arrangement.SpaceEvenly) {
        TextRadioButton(
            currentSelectMode = selectMode,
            requiredSelectMode = SelectMode.AttendingStudents,
            onSelectModeChanged = onSelectModeChanged,
            text = "Присутствующие"
        )
        TextRadioButton(
            currentSelectMode = selectMode,
            requiredSelectMode = SelectMode.MissingStudents,
            onSelectModeChanged = onSelectModeChanged,
            text = "Отсутствующие"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TextRadioButton(
    modifier: Modifier = Modifier,
    currentSelectMode: SelectMode,
    requiredSelectMode: SelectMode,
    onSelectModeChanged: (SelectMode) -> Unit,
    text: String
) {
    Card(
        modifier = modifier,
        shape = CircleShape,
        elevation = 0.dp,
        onClick = { onSelectModeChanged(requiredSelectMode) }
    ) {
        Row(Modifier.padding(8.dp)) {
            RadioButton(
                selected = currentSelectMode == requiredSelectMode,
                onClick = null
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text, modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}