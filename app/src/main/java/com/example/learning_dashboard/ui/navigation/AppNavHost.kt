package com.example.learning_dashboard.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learning_dashboard.ui.dashboard.DashboardRoute
import com.example.learning_dashboard.ui.detail.CourseDetailRoute
import com.example.learning_dashboard.ui.login.LoginRoute
import kotlinx.serialization.Serializable

@Serializable
data object LoginDestination

@Serializable
data object DashboardDestination

@Serializable
data class CourseDetailDestination(val courseId: Long)

private const val TRANSITION_MS = 250

@Composable
fun AppNavHost(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val startDestination: Any = remember { if (isLoggedIn) DashboardDestination else LoginDestination }

    // Session changes (login / logout) reset the back stack.
    LaunchedEffect(isLoggedIn) {
        val onLogin = navController.currentDestination?.hasRoute<LoginDestination>() == true
        if (isLoggedIn == onLogin) {
            val target: Any = if (isLoggedIn) DashboardDestination else LoginDestination
            navController.navigate(target) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        // Login <-> dashboard swaps the whole session, so it cuts instantly instead of
        // cross-fading the old screen behind the new one. In-session screens slide.
        enterTransition = {
            if (isSessionSwitch()) EnterTransition.None
            else slideIntoContainer(SlideDirection.Start, tween(TRANSITION_MS))
        },
        exitTransition = {
            if (isSessionSwitch()) ExitTransition.None
            else fadeOut(tween(TRANSITION_MS))
        },
        popEnterTransition = { fadeIn(tween(TRANSITION_MS)) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(TRANSITION_MS)) },
    ) {
        composable<LoginDestination> {
            LoginRoute()
        }
        composable<DashboardDestination> { entry ->
            DashboardRoute(
                onCourseClick = { courseId ->
                    if (entry.isResumed()) navController.navigate(CourseDetailDestination(courseId))
                },
            )
        }
        composable<CourseDetailDestination> { entry ->
            CourseDetailRoute(onBack = { if (entry.isResumed()) navController.popBackStack() })
        }
    }
}

// Ignores repeat taps while a transition is running, e.g. a double tap on Back
// popping the dashboard as well.
private fun NavBackStackEntry.isResumed(): Boolean =
    lifecycle.currentState == Lifecycle.State.RESUMED

private fun AnimatedContentTransitionScope<NavBackStackEntry>.isSessionSwitch(): Boolean =
    initialState.destination.hasRoute<LoginDestination>() ||
        targetState.destination.hasRoute<LoginDestination>()
