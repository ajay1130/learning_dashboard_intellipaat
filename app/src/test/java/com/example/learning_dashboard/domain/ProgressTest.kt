package com.example.learning_dashboard.domain

import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.domain.model.progressPercent
import org.junit.Assert.assertEquals
import org.junit.Test

class ProgressTest {

    @Test
    fun `progress rounds down and never reports 100 before all lessons are done`() {
        assertEquals(33, progressPercent(completed = 1, total = 3))
        assertEquals(99, progressPercent(completed = 199, total = 200))
        assertEquals(100, progressPercent(completed = 3, total = 3))
    }

    @Test
    fun `course without lessons has zero progress instead of dividing by zero`() {
        assertEquals(0, progressPercent(completed = 0, total = 0))
    }

    @Test
    fun `completing a lesson moves course progress`() {
        val lessons = listOf(
            Lesson(1, "Introduction", isCompleted = true),
            Lesson(2, "Variables", isCompleted = false),
            Lesson(3, "Functions", isCompleted = false),
            Lesson(4, "OOP", isCompleted = false),
        )
        val course = CourseDetail(1, "Python", "John Smith", lessons)
        assertEquals(25, course.progress)

        val updated = course.copy(
            lessons = lessons.map { if (it.id == 2L) it.copy(isCompleted = true) else it }
        )
        assertEquals(50, updated.progress)
        assertEquals(2, updated.completedLessons)
    }
}
