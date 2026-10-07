package com.example.learning_dashboard.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BrandColors = lightColorScheme(
    primary = BrandOrange,
    onPrimary = Color.White,
    primaryContainer = BrandOrangeSoft,
    onPrimaryContainer = BrandOrangeDark,
    secondary = BrandIndigo,
    onSecondary = Color.White,
    secondaryContainer = BrandIndigoSoft,
    onSecondaryContainer = BrandIndigo,
    tertiary = Success,
    tertiaryContainer = SuccessSoft,
    onTertiaryContainer = Success,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = SkyTint,
    onSurfaceVariant = InkMuted,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = Hairline,
    outline = FieldBorder,
    outlineVariant = Hairline,
    error = Danger,
    errorContainer = Color(0xFFFDECEA),
    onErrorContainer = Danger,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
)

/**
 * Always light: the brand logo and palette are designed for white backgrounds,
 * and dynamic colour would override the brand colours.
 */
@Composable
fun Learning_dashboardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BrandColors,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
