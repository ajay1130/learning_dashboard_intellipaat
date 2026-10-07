package com.example.learning_dashboard.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.learning_dashboard.R

@Composable
fun BrandLogo(modifier: Modifier = Modifier, height: Dp = 32.dp) {
    Image(
        painter = painterResource(R.drawable.intellipaat_logo),
        contentDescription = stringResource(R.string.brand_name),
        contentScale = ContentScale.FillHeight,
        modifier = modifier.height(height),
    )
}

/** "[highlight] rest" with the highlighted part in the brand colour. */
@Composable
fun highlightedText(prefix: String = "", highlight: String, suffix: String = ""): AnnotatedString {
    val accent = MaterialTheme.colorScheme.primary
    return buildAnnotatedString {
        append(prefix)
        withStyle(SpanStyle(color = accent)) { append(highlight) }
        append(suffix)
    }
}
