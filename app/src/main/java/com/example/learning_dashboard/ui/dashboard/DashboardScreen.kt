package com.example.learning_dashboard.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learning_dashboard.R
import com.example.learning_dashboard.domain.model.Course
import com.example.learning_dashboard.ui.common.BrandLogo
import com.example.learning_dashboard.ui.common.LoadingView
import com.example.learning_dashboard.ui.common.MessageView
import com.example.learning_dashboard.ui.common.highlightedText
import com.example.learning_dashboard.ui.theme.Learning_dashboardTheme

@Composable
fun DashboardRoute(
    onCourseClick: (Long) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        state = state,
        onCourseClick = onCourseClick,
        onRefresh = viewModel::refresh,
        onLogout = viewModel::onLogoutClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardUiState,
    onCourseClick: (Long) -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { BrandLogo(height = 30.dp) },
                actions = {
                    TextButton(onClick = onLogout) { Text(stringResource(R.string.logout)) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { padding ->
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(padding)

        when (state) {
            DashboardUiState.Loading -> LoadingView(contentModifier)

            DashboardUiState.Empty -> MessageView(
                title = stringResource(R.string.empty_title),
                message = stringResource(R.string.empty_courses),
                glyph = "\u2606",
                onRetry = onRefresh,
                modifier = contentModifier,
            )

            is DashboardUiState.Error -> MessageView(
                title = stringResource(R.string.error_title),
                message = stringResource(state.message),
                onRetry = onRefresh,
                modifier = contentModifier,
            )

            is DashboardUiState.Content -> PullToRefreshBox(
                isRefreshing = state.isRefreshing,
                onRefresh = onRefresh,
                modifier = contentModifier,
            ) {
                DashboardContent(state, onCourseClick)
            }
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState.Content,
    onCourseClick: (Long) -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        if (state.staleDataMessage != null) {
            StaleDataBanner(
                message = stringResource(state.staleDataMessage),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp),
            )
        }
        CourseList(state, onCourseClick)
    }
}

@Composable
private fun CourseList(
    state: DashboardUiState.Content,
    onCourseClick: (Long) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "summary") { SummaryCard(state) }
        item(key = "section") {
            Text(
                stringResource(R.string.continue_learning),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        items(state.courses, key = { it.id }) { course ->
            CourseCard(course = course, onClick = { onCourseClick(course.id) })
        }
    }
}

@Composable
private fun SummaryCard(state: DashboardUiState.Content) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = highlightedText(
                    prefix = stringResource(R.string.dashboard_headline_prefix),
                    highlight = stringResource(R.string.dashboard_headline_highlight),
                ),
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(
                    value = state.courses.size.toString(),
                    label = stringResource(R.string.stat_courses),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = stringResource(R.string.progress_percent, state.overallProgress),
                    label = stringResource(R.string.stat_progress),
                    accent = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    value = "${state.completedLessons}/${state.totalLessons}",
                    label = stringResource(R.string.stat_lessons),
                    accent = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.onSurface,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = accent, maxLines = 1)
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun StaleDataBanner(message: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun CourseCard(course: Course, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CourseBadge(course)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        course.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        stringResource(R.string.instructor_by, course.instructor),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { course.progress / 100f },
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            )
            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.progress_complete, course.progress),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        stringResource(R.string.lesson_count, course.lessonCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Button(
                    onClick = onClick,
                    shape = MaterialTheme.shapes.small,
                    contentPadding = ButtonDefaults.ContentPadding,
                ) {
                    Text(stringResource(course.actionLabel()))
                }
            }
        }
    }
}

@Composable
private fun CourseBadge(course: Course) {
    val (container, content) = when ((course.id % 3).toInt()) {
        0 -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        1 -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(container, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center,
    ) {
        Text(course.initials(), style = MaterialTheme.typography.titleMedium, color = content)
    }
}

private fun Course.initials(): String =
    title.split(' ').filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() }

private fun Course.actionLabel(): Int = when {
    completedLessons == 0 -> R.string.start_course
    lessonCount > 0 && completedLessons >= lessonCount -> R.string.review_course
    else -> R.string.continue_course
}

@Preview(showBackground = true)
@Composable
private fun DashboardPreview() {
    Learning_dashboardTheme {
        DashboardScreen(
            state = DashboardUiState.Content(
                courses = listOf(
                    Course(1, "Python Programming", "John Smith", lessonCount = 8, completedLessons = 5),
                    Course(2, "Generative AI", "Sarah Williams", lessonCount = 5, completedLessons = 2),
                    Course(3, "Full Stack Development", "David Brown", lessonCount = 8, completedLessons = 0),
                ),
                isRefreshing = false,
                staleDataMessage = R.string.offline_banner,
            ),
            onCourseClick = {},
            onRefresh = {},
            onLogout = {},
        )
    }
}
