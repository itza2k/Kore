package com.itza2k.kore.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


val KoreGreen = Color(0xFF4CAF50)
val KoreGreenSecondary = KoreGreen.copy(alpha = 0.7f)
val KoreGreenTertiary = KoreGreen.copy(alpha = 0.5f)

private val KoreDarkColorScheme = darkColorScheme(
    primary = KoreGreen,
    secondary = KoreGreenSecondary,
    tertiary = KoreGreenTertiary,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)
private val KoreLightColorScheme = lightColorScheme(
    primary = KoreGreen,
    secondary = KoreGreenSecondary,
    tertiary = KoreGreenTertiary,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF121212),
    onSurface = Color(0xFF121212)
)
val KoreShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)

@Composable
fun KoreTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) KoreDarkColorScheme else KoreLightColorScheme
    val typography = getTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = KoreShapes,
        content = content
    )
}

@Composable
fun getTypography(): Typography {
    return Typography(
        displayLarge = MaterialTheme.typography.displayLarge.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Light
        ),
        displayMedium = MaterialTheme.typography.displayMedium.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Light
        ),
        displaySmall = MaterialTheme.typography.displaySmall.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal
        ),
        headlineLarge = MaterialTheme.typography.headlineLarge.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        headlineMedium = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        headlineSmall = MaterialTheme.typography.headlineSmall.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        titleLarge = MaterialTheme.typography.titleLarge.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        titleMedium = MaterialTheme.typography.titleMedium.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        titleSmall = MaterialTheme.typography.titleSmall.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        ),
        bodyLarge = MaterialTheme.typography.bodyLarge,
        bodyMedium = MaterialTheme.typography.bodyMedium,
        bodySmall = MaterialTheme.typography.bodySmall,
        labelLarge = MaterialTheme.typography.labelLarge.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        labelMedium = MaterialTheme.typography.labelMedium.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        ),
        labelSmall = MaterialTheme.typography.labelSmall.copy(
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    )
}
