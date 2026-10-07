package com.example.learning_dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learning_dashboard.ui.navigation.AppNavHost
import com.example.learning_dashboard.ui.splash.SplashScreen
import com.example.learning_dashboard.ui.theme.Learning_dashboardTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The UI is always light, so keep dark system bar icons regardless of system theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            Learning_dashboardTheme {
                Surface(Modifier.fillMaxSize()) {
                    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                    var splashShown by rememberSaveable { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        delay(SPLASH_MIN_DURATION_MS)
                        splashShown = true
                    }

                    val session = isLoggedIn
                    if (!splashShown || session == null) {
                        SplashScreen()
                    } else {
                        AppNavHost(isLoggedIn = session)
                    }
                }
            }
        }
    }

    private companion object {
        const val SPLASH_MIN_DURATION_MS = 1200L
    }
}
