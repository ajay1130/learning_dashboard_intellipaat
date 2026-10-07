package com.example.learning_dashboard.data.remote

import com.example.learning_dashboard.data.remote.dto.CourseDto

interface CourseApi {
    suspend fun getCourses(): List<CourseDto>
}
