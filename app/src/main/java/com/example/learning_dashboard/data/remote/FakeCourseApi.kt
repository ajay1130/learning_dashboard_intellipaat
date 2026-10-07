package com.example.learning_dashboard.data.remote

import android.content.Context
import com.example.learning_dashboard.data.remote.dto.CourseDto
import com.example.learning_dashboard.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Serves courses from assets but behaves like a remote call: it has latency and
 * fails when the device is offline, so the offline path can be exercised for real.
 */
class FakeCourseApi @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
    private val connectivity: ConnectivityChecker,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : CourseApi {

    override suspend fun getCourses(): List<CourseDto> = withContext(ioDispatcher) {
        delay(800.milliseconds)
        if (!connectivity.isOnline()) throw IOException("No internet connection")

        val body = context.assets.open("courses.json").bufferedReader().use { it.readText() }
        json.decodeFromString<List<CourseDto>>(body)
    }
}
