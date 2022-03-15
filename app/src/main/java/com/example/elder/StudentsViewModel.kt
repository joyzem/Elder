package com.example.elder

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.elder.ui.model.GroupReport
import com.example.elder.ui.model.Lesson
import com.example.elder.ui.model.Student
import java.text.DateFormat
import java.util.*

class StudentsViewModel : ViewModel() {

    private val group03 = listOf(
        "Ахмед-Оглы",
        "Букач",
        "Буренко",
        "Гертер",
        "Гусарь",
        "Давоян",
        "Евсиков",
        "Каунов",
        "Коротаева",
        "Кравченко",
        "Куприянов",
        "Куприянова",
        "Мирошников",
        "Низами",
        "Ольховский",
        "Переверзев",
        "Пивнев",
        "Полутина",
        "Розентул",
        "Сапрыкина",
        "Ханцев",
        "Шелехов",
        "Шепилов"
    )

    private val group01 = listOf(
        "Васильев",
        "Гайфуллин",
        "Галась",
        "Глушаков",
        "Дерябкина",
        "Долотов",
        "Егоров",
        "Ивлев",
        "Кириллов",
        "Клеменко",
        "Корепанов",
        "Косников",
        "Котов",
        "Муравьев",
        "Ней",
        "Пруцаков",
        "Стасенко",
        "Степанов",
        "Факхур",
        "Федоренко",
        "Фоменко",
        "Химичева",
        "Чайников",
        "Чернобровкина",
        "Шипулин",
        "Щербина",
        "Яворская"
    )

    private val group02 = listOf(
        "Болотов",
        "Винниченко",
        "Волков",
        "Газизов",
        "Глушков",
        "Григорьев",
        "Гусейнов",
        "Данилов",
        "Денисов",
        "Довгаль",
        "Зароченцев",
        "Жадан",
        "Карими",
        "Криворучко",
        "Кост",
        "Лысенко",
        "Медведев",
        "Панина",
        "Попов",
        "Петрина",
        "Овсепьян",
        "Самойлик",
        "Тищенко",
        "Ткаченко",
        "Харитонов",
        "Черногаев"
    )

    val group = group02.map { Student(it) }.toMutableStateList()

    var date: Calendar by mutableStateOf(Calendar.getInstance())

    var lesson by mutableStateOf(Lesson.FIRST)

    fun onStudentChecked(student: Student) {
        student.checked.value = !student.checked.value
    }

    fun onDateChanged(newDate: Calendar) {
        date = newDate
    }

    fun onLessonChanged(newLesson: Lesson) {
        lesson = newLesson
    }

    fun onAttendingStudentsRequest(): GroupReport {
        val attendingStudents = group.filter { student -> student.checked.value }
        return createReport(attendingStudents, "Присутствующие:")
    }

    fun onMissingStudentsRequest(): GroupReport {
        val missingStudents = group.filter { student -> !student.checked.value }
        return createReport(missingStudents, "Отсутствующие:")
    }

    fun checkAllStudents() {
        group.forEach { student -> student.checked.value = !student.checked.value}
    }

    private fun createReport(group: List<Student>, prefix: String): GroupReport {
        val groupReport = GroupReport(
            subject = "ПИ2002, ${DateFormat.getDateInstance().format(date.time)}, ${lesson.value}",
            content = group.joinToString(
                separator = "\n",
                prefix = "${prefix}\n",
                transform = { student ->
                    student.name
                })
        )
        return groupReport
    }
}