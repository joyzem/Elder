package com.example.elder.data

import com.example.elder.data.students.StudentEntity

class Repository {
    companion object {
        fun getGroup(groupKey: GROUP_KEYS): List<StudentEntity> {
            when (groupKey) {
                GROUP_KEYS.GROUP01 -> return group01.map { StudentEntity(surname = it) }
                GROUP_KEYS.GROUP02 -> return group02.map { StudentEntity(surname = it) }
                GROUP_KEYS.GROUP03 -> return group03.map { StudentEntity(surname = it) }
            }
        }

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
    }
}

enum class GROUP_KEYS{
    GROUP01,
    GROUP02,
    GROUP03
}