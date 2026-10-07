package com.example.learning_dashboard.ui.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learning_dashboard.R
import com.example.learning_dashboard.domain.model.CourseDetail
import com.example.learning_dashboard.domain.model.Lesson
import com.example.learning_dashboard.ui.common.LoadingView
import com.example.learning_dashboard.ui.common.MessageView
import com.example.learning_dashboard.ui.theme.Learning_dashboardTheme

@Composable
fun CourseDetailRoute(
    onBack: () -> Unit,
    viewModel: CourseDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CourseDetailScreen(state = state, onBack = onBack, onMarkCompleted = viewModel::onMarkCompleted)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    state: CourseDetailUiState,
    onBack: () -> Unit,
    onMarkCompleted: (Long) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        val modifier = Modifier
            .fillMaxSize()
            .padding(padding)

        when (state) {
            CourseDetailUiState.Loading -> LoadingView(modifier)
            CourseDetailUiState.NotFound -> MessageView(stringResource(R.string.course_not_found), modifier)
            is CourseDetailUiState.Content -> CourseDetailContent(state.course, onMarkCompleted, modifier)
        }
    }
}

@Composable
private fun CourseDetailContent(
    course: CourseDetail,
    onMarkCompleted: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier, contentPadding = PaddingValues(bottom = 24.dp)) {
        item(key = "header") {
            CourseHeader(course, Modifier.padding(horizontal = 16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp),
            ) {
                Text(
                    stringResource(R.string.lessons),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    stringResource(R.string.lessons_ratio, course.completedLessons, course.lessons.size),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        itemsIndexed(course.lessons, key = { _, lesson -> lesson.id }) { index, lesson ->
            LessonRow(
                number = index + 1,
                lesson = lesson,
                onMarkCompleted = { onMarkCompleted(lesson.id) },
            )
            if (index < course.lessons.lastIndex) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(start = 68.dp, end = 20.dp),
                )
            }
        }
    }
}

@Composable
private fun CourseHeader(course: CourseDetail, modifier: Modifier = Modifier) {
    val isDone = course.lessons.isNotEmpty() && course.completedLessons == course.lessons.size

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.course_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(course.title, style = MaterialTheme.typography.headlineSmall)
            Text(
                stringResource(R.string.instructor_by, course.instructor),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { course.progress / 100f },
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        strokeWidth = 7.dp,
                        modifier = Modifier.size(76.dp),
                    )
                    Text(
                        stringResource(R.string.progress_percent, course.progress),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        stringResource(R.string.course_progress, course.completedLessons, course.lessons.size),
                        style = MaterialTheme.typography.titleSmall,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(if (isDone) R.string.course_done_message else R.string.course_keep_going),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun LessonRow(number: Int, lesson: Lesson, onMarkCompleted: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        LessonMarker(number, lesson.isCompleted)
        Spacer(Modifier.width(16.dp))

        Column(Modifier.weight(1f)) {
            Text(lesson.title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = if (lesson.isCompleted) {
                    "\u2713 " + stringResource(R.string.lesson_completed)
                } else {
                    "\u25CB " + stringResource(R.string.lesson_pending)
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (lesson.isCompleted) {
                    MaterialTheme.colorScheme.tertiary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }

        if (!lesson.isCompleted) {
            OutlinedButton(
                onClick = onMarkCompleted,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) {
                Text(stringResource(R.string.mark_complete), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun LessonMarker(number: Int, completed: Boolean) {
    val markerModifier = Modifier.size(32.dp)
    if (completed) {
        Box(
            markerModifier.background(MaterialTheme.colorScheme.tertiary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("\u2713", color = Color.White, fontWeight = FontWeight.Bold)
        }
    } else {
        Box(
            markerModifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                number.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseDetailPreview() {
    Learning_dashboardTheme {
        CourseDetailScreen(
            state = CourseDetailUiState.Content(
                CourseDetail(
                    id = 1,
                    title = "Python Programming",
                    instructor = "John Smith",
                    lessons = listOf(
                        Lesson(1, "Introduction", isCompleted = true),
                        Lesson(2, "Variables & Data Types", isCompleted = true),
                        Lesson(3, "Functions", isCompleted = false),
                        Lesson(4, "OOP", isCompleted = false),
                    ),
                ),
            ),
            onBack = {},
            onMarkCompleted = {},
        )
    }
}
