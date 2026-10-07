package com.example.learning_dashboard.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.learning_dashboard.R
import com.example.learning_dashboard.ui.common.BrandLogo
import com.example.learning_dashboard.ui.theme.Learning_dashboardTheme

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Balances the tagline below so the logo sits dead centre, matching the system splash icon.
            Spacer(Modifier.height(55.dp))
            BrandLogo(height = 56.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                stringResource(R.string.splash_tagline),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.extraSmall)
            )
        }

        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
                .size(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashPreview() {
    Learning_dashboardTheme { SplashScreen() }
}
