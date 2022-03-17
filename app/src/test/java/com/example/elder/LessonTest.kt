package com.example.elder

import com.example.elder.domain.Lesson
import com.example.elder.domain.getCurrentLesson
import org.junit.Test
import java.util.*

class LessonTest {

    @Test
    fun beforeFirstLessonStarted() {
        val lessonTime = getLessonTime(5, 30)
        assert(getCurrentLesson(lessonTime) == Lesson.FIRST)
    }

    @Test
    fun duringFirstLesson() {
        val lessonTime = getLessonTime(8, 30)
        assert(getCurrentLesson(lessonTime) == Lesson.FIRST)
    }

    @Test
    fun beforeSecondLessonStarted() {
        val lessonTime = getLessonTime(9, 34)
        assert(getCurrentLesson(lessonTime) == Lesson.FIRST)
    }

    @Test
    fun duringSecondLesson() {
        val lessonTime = getLessonTime(10, 30)
        assert(getCurrentLesson(lessonTime) == Lesson.SECOND)
    }

    @Test
    fun beforeThirdLessonStarted() {
        val lessonTime = getLessonTime(11, 20)
        assert(getCurrentLesson(lessonTime) == Lesson.SECOND)
    }

    @Test
    fun duringThirdLesson() {
        val lessonTime = getLessonTime(12, 0)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(getCurrentLesson(lessonTime) == Lesson.THIRD)
    }

    @Test
    fun beforeFourthLessonStarted() {
        val lessonTime = getLessonTime(13, 30)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.THIRD)
    }

    @Test
    fun duringFourthLesson() {
        val lessonTime = getLessonTime(14, 0)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.FOURTH)
    }

    @Test
    fun beforeFifthLessonStarted() {
        val lessonTime = getLessonTime(15, 30)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.FOURTH)
    }

    @Test
    fun duringFifthLesson() {
        val lessonTime = getLessonTime(16, 30)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.FIFTH)
    }

    @Test
    fun beforeSixthLessonStarted() {
        val lessonTime = getLessonTime(17, 10)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.FIFTH)
    }

    @Test
    fun duringSixthLesson() {
        val lessonTime = getLessonTime(18, 30)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.SIXTH)
    }

    @Test
    fun afterSixthLesson() {
        val lessonTime = getLessonTime(20, 30)
        val currentLesson = getCurrentLesson(lessonTime)
        assert(currentLesson == Lesson.SIXTH)
    }

    private fun getLessonTime(hour: Int, minute: Int): Calendar {
        val c = Calendar.getInstance()
        return c.apply { this.set(Calendar.HOUR_OF_DAY, hour); this.set(Calendar.MINUTE, minute)}
    }
}