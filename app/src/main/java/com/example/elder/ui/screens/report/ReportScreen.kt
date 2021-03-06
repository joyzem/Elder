package com.example.elder.ui.screens.report

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.elder.R
import com.example.elder.domain.GroupReport
import com.example.elder.domain.Lesson
import com.example.elder.domain.SwipeDirection
import com.example.elder.ui.components.ClickableCard
import com.example.elder.ui.components.ElderOutlinedButton
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ReportScreen(
    reportViewModel: ReportViewModel,
    groupName: String,
    onSendReport: (GroupReport) -> Unit,
    onSwipe: (SwipeDirection) -> Unit
) {
    val backdropScaffoldState =
        rememberBackdropScaffoldState(initialValue = BackdropValue.Revealed)
    var showSendDialog by rememberSaveable {
        mutableStateOf(false)
    }
    if (showSendDialog) {
        SendDialog(
            reportViewModel = reportViewModel,
            onDismissRequest = { showSendDialog = false },
            onSendReport = onSendReport
        )
    }
    val scope = rememberCoroutineScope()
    BackdropScaffold(
        scaffoldState = backdropScaffoldState,
        appBar = {
            ReportTopAppBar(
                reportViewModel,
                onSendClicked = {
                    showSendDialog = true
                },
                onMenuClicked = {
                    scope.launch {
                        backdropScaffoldState.reveal()
                    }
                }
            )
        },
        backLayerContent = {
            ReportBackLayer(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                reportViewModel = reportViewModel,
                onSendClicked = { showSendDialog = true },
                groupName = groupName
            )
        },
        persistentAppBar = false,
        frontLayerContent = {
            ReportFrontLayer(
                reportViewModel = reportViewModel
            )
        },
        gesturesEnabled = true,
        frontLayerElevation = 8.dp,
        backLayerBackgroundColor = MaterialTheme.colors.surface,
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
private fun ReportFrontLayer(
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel
) {
    val students = reportViewModel.students
    Scaffold(modifier = modifier) {
        if (students.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Text(
                    text = "?????????????????? ?? ???????????? \"????????????\", ?????????? ?????????????????? ????????????",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
        LazyColumn {
            itemsIndexed(items = students) { index, student ->
                StudentRow(
                    onStudentChecked = {
                        reportViewModel.onStudentChecked(student)
                    },
                    reportStudentUiState = student,
                    index = index
                )
                Divider()
            }
        }
    }
}

@Composable
private fun StudentRow(
    onStudentChecked: (Boolean) -> Unit,
    reportStudentUiState: ReportStudentUiState,
    index: Int
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            AnimatedVisibilityCheckbox(reportStudentUiState.checked)
            Text("${index + 1}. ${reportStudentUiState.student.surname}")
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
                Text(text = if (reportStudentUiState.checked) "????????????????????????" else "??????????????????????")
            }
        }
        AnimatedVisibility(visible = !reportStudentUiState.hasReason.value && !reportStudentUiState.checked) {
            TextButton(onClick = { reportStudentUiState.hasReason.value = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Text(text = "?????????????? ??????????????")
            }
        }
        AnimatedVisibility(visible = reportStudentUiState.hasReason.value && !reportStudentUiState.checked) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = reportStudentUiState.reasonOfMissing.value,
                onValueChange = { reportStudentUiState.reasonOfMissing.value = it },
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .fillMaxWidth(),
                label = { Text("?????????????? ???????????????????? (??????????????????????????)") },
                textStyle = TextStyle.Default.copy(fontSize = 18.sp),
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )
        }
    }
}

@Composable
private fun RowScope.AnimatedVisibilityCheckbox(visible: Boolean) {
    AnimatedVisibility(
        visible = visible
    ) {
        Checkbox(
            checked = visible, onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colors.primary
            )
        )
    }
    AnimatedVisibility(visible = visible) {
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
private fun ReportBackLayer(
    modifier: Modifier = Modifier,
    reportViewModel: ReportViewModel,
    groupName: String,
    onSendClicked: () -> Unit
) {
    Surface(modifier = modifier) {
        Column(Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            var showDialog by remember { mutableStateOf(false) }
            Text(
                text = groupName,
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.h6
            )
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
            Spacer(modifier = Modifier.height(12.dp))
            ElderOutlinedButton(
                onClick = onSendClicked,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("??????????????????")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.Send, contentDescription = null)
                }
            }
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
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "????????", style = MaterialTheme.typography.h6)
        Spacer(Modifier.width(16.dp))
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
                iconDescription = "?????????????? ????????"
            )
        }
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
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
        Text(text = "????????", style = MaterialTheme.typography.h6)
        Spacer(Modifier.width(16.dp))
        ClickableCard(modifier = modifier, onClick = onShowDialogButton) {
            TextSpaceIconRow(
                modifier = Modifier.padding(16.dp),
                text = lesson.value,
                icon = painterResource(id = R.drawable.ic_time),
                iconDescription = "?????????????? ????????"
            )
        }
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
                .fillMaxWidth(),
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
private fun SendDialog(
    reportViewModel: ReportViewModel,
    onDismissRequest: () -> Unit,
    onSendReport: (GroupReport) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(text = "???????????????? ????????????", Modifier.padding(16.dp))
                Divider(Modifier.height(1.dp))
                Text(
                    text = "????????????????????????????",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.AttendingStudents)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Text(
                    text = "??????????????????????????",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.MissingStudents)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Text(
                    text = "??????",
                    modifier = Modifier
                        .clickable {
                            reportViewModel.onSelectModeChanged(SelectMode.Both)
                            onSendReport(reportViewModel.getReport())
                            onDismissRequest()
                        }
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
