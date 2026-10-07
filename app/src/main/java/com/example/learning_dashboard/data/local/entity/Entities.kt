package com.example.learning_dashboard.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val instructor: String,
    val position: Int,
)

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("courseId")],
)
data class LessonEntity(
    @PrimaryKey val id: Long,
    val courseId: Long,
    val position: Int,
    val title: String,
    val completed: Boolean,
)

data class CourseWithLessons(
    @Embedded val course: CourseEntity,
    @Relation(parentColumn = "id", entityColumn = "courseId")
    val lessons: List<LessonEntity>,
)

data class CourseSummaryRow(
    val id: Long,
    val title: String,
    val instructor: String,
    val lessonCount: Int,
    val completedCount: Int,
)
