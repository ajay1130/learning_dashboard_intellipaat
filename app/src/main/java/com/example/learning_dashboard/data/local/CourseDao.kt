package com.example.learning_dashboard.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.learning_dashboard.data.local.entity.CourseEntity
import com.example.learning_dashboard.data.local.entity.CourseSummaryRow
import com.example.learning_dashboard.data.local.entity.CourseWithLessons
import com.example.learning_dashboard.data.local.entity.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CourseDao {

    @Query(
        """
        SELECT c.id, c.title, c.instructor,
               COUNT(l.id) AS lessonCount,
               COALESCE(SUM(l.completed), 0) AS completedCount
        FROM courses c
        LEFT JOIN lessons l ON l.courseId = c.id
        GROUP BY c.id
        ORDER BY c.position
        """
    )
    abstract fun observeCourseSummaries(): Flow<List<CourseSummaryRow>>

    @Transaction
    @Query("SELECT * FROM courses WHERE id = :courseId")
    abstract fun observeCourse(courseId: Long): Flow<CourseWithLessons?>

    @Query("UPDATE lessons SET completed = 1 WHERE id = :lessonId")
    abstract suspend fun markLessonCompleted(lessonId: Long)

    /**
     * Replaces the cached catalogue with [courses]/[lessons] from the server.
     * Lessons the user already completed on this device stay completed even if
     * the server hasn't caught up yet.
     */
    @Transaction
    open suspend fun sync(courses: List<CourseEntity>, lessons: List<LessonEntity>) {
        val completedLocally = completedLessonIds().toSet()

        deleteCoursesNotIn(courses.map { it.id })
        deleteLessonsNotIn(lessons.map { it.id })
        upsertCourses(courses)
        upsertLessons(
            lessons.map { if (it.id in completedLocally) it.copy(completed = true) else it }
        )
    }

    @Query("DELETE FROM courses")
    abstract suspend fun clear()

    @Query("SELECT id FROM lessons WHERE completed = 1")
    abstract suspend fun completedLessonIds(): List<Long>

    @Query("DELETE FROM courses WHERE id NOT IN (:ids)")
    abstract suspend fun deleteCoursesNotIn(ids: List<Long>)

    @Query("DELETE FROM lessons WHERE id NOT IN (:ids)")
    abstract suspend fun deleteLessonsNotIn(ids: List<Long>)

    @Upsert
    abstract suspend fun upsertCourses(courses: List<CourseEntity>)

    @Upsert
    abstract suspend fun upsertLessons(lessons: List<LessonEntity>)
}
