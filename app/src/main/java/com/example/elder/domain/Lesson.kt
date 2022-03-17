package com.example.elder.domain

import java.util.*

enum class Lesson(
    val value: String
) {
    FIRST("1-ая пара"),
    SECOND("2-ая пара"),
    THIRD("3-я пара"),
    FOURTH("4-ая пара"),
    FIFTH("5-ая пара"),
    SIXTH("6-ая пара")
}

fun getCurrentLesson(c: Calendar = Calendar.getInstance()): Lesson {
    val hours = c.get(Calendar.HOUR_OF_DAY)
    val minutes = c.get(Calendar.MINUTE)
    return when (hours) {
        in 0..8 -> Lesson.FIRST
        9 -> if (minutes < 45) Lesson.FIRST else Lesson.SECOND
        10 -> Lesson.SECOND
        11 -> if (minutes < 30) Lesson.SECOND else Lesson.THIRD
        12 -> Lesson.THIRD
        13 -> if (minutes < 50) Lesson.THIRD else Lesson.FOURTH
        14 -> Lesson.FOURTH
        15 -> if (minutes < 35) Lesson.FOURTH else Lesson.FIFTH
        16 -> Lesson.FIFTH
        17 -> if (minutes < 20) Lesson.FIFTH else Lesson.SIXTH
        else -> Lesson.SIXTH
    }
}