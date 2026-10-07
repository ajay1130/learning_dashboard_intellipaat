package com.example.learning_dashboard.domain.model

data class Course(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedLessons: Int,
) {
    val progress: Int get() = progressPercent(completedLessons, lessonCount)
}

data class CourseDetail(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessons: List<Lesson>,
) {
    val completedLessons: Int get() = lessons.count { it.isCompleted }
    val progress: Int get() = progressPercent(completedLessons, lessons.size)
}

data class Lesson(
    val id: Long,
    val title: String,
    val isCompleted: Boolean,
)
