package com.ab.datawatch.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ab.datawatch.data.model.ThemeColor

private val BlueLightColors = lightColorScheme(
    primary = BlueLightPrimary, onPrimary = BlueLightOnPrimary,
    primaryContainer = BlueLightPrimaryContainer, onPrimaryContainer = BlueLightOnPrimaryContainer,
    secondary = BlueLightSecondary, onSecondary = BlueLightOnSecondary,
    secondaryContainer = BlueLightSecondaryContainer, onSecondaryContainer = BlueLightOnSecondaryContainer,
    tertiary = BlueLightTertiary, onTertiary = BlueLightOnTertiary,
    tertiaryContainer = BlueLightTertiaryContainer, onTertiaryContainer = BlueLightOnTertiaryContainer,
    background = BlueLightBackground, onBackground = BlueLightOnBackground,
    surface = BlueLightSurface, onSurface = BlueLightOnSurface,
    surfaceVariant = BlueLightSurfaceVariant, onSurfaceVariant = BlueLightOnSurfaceVariant,
    outline = BlueLightOutline, outlineVariant = BlueLightOutlineVariant,
    error = BlueLightError, onError = BlueLightOnError,
    errorContainer = BlueLightErrorContainer, onErrorContainer = BlueLightOnErrorContainer
)

private val BlueDarkColors = darkColorScheme(
    primary = BlueDarkPrimary, onPrimary = BlueDarkOnPrimary,
    primaryContainer = BlueDarkPrimaryContainer, onPrimaryContainer = BlueDarkOnPrimaryContainer,
    secondary = BlueDarkSecondary, onSecondary = BlueDarkOnSecondary,
    secondaryContainer = BlueDarkSecondaryContainer, onSecondaryContainer = BlueDarkOnSecondaryContainer,
    tertiary = BlueDarkTertiary, onTertiary = BlueDarkOnTertiary,
    tertiaryContainer = BlueDarkTertiaryContainer, onTertiaryContainer = BlueDarkOnTertiaryContainer,
    background = BlueDarkBackground, onBackground = BlueDarkOnBackground,
    surface = BlueDarkSurface, onSurface = BlueDarkOnSurface,
    surfaceVariant = BlueDarkSurfaceVariant, onSurfaceVariant = BlueDarkOnSurfaceVariant,
    outline = BlueDarkOutline, outlineVariant = BlueDarkOutlineVariant,
    error = BlueDarkError, onError = BlueDarkOnError,
    errorContainer = BlueDarkErrorContainer, onErrorContainer = BlueDarkOnErrorContainer
)

private val OrangeLightColors = lightColorScheme(
    primary = OrangeLightPrimary, onPrimary = OrangeLightOnPrimary,
    primaryContainer = OrangeLightPrimaryContainer, onPrimaryContainer = OrangeLightOnPrimaryContainer,
    secondary = OrangeLightSecondary, onSecondary = OrangeLightOnSecondary,
    secondaryContainer = OrangeLightSecondaryContainer, onSecondaryContainer = OrangeLightOnSecondaryContainer,
    tertiary = OrangeLightTertiary, onTertiary = OrangeLightOnTertiary,
    tertiaryContainer = OrangeLightTertiaryContainer, onTertiaryContainer = OrangeLightOnTertiaryContainer,
    background = OrangeLightBackground, onBackground = OrangeLightOnBackground,
    surface = OrangeLightSurface, onSurface = OrangeLightOnSurface,
    surfaceVariant = OrangeLightSurfaceVariant, onSurfaceVariant = OrangeLightOnSurfaceVariant,
    outline = OrangeLightOutline, outlineVariant = OrangeLightOutlineVariant,
    error = OrangeLightError, onError = OrangeLightOnError,
    errorContainer = OrangeLightErrorContainer, onErrorContainer = OrangeLightOnErrorContainer
)

private val OrangeDarkColors = darkColorScheme(
    primary = OrangeDarkPrimary, onPrimary = OrangeDarkOnPrimary,
    primaryContainer = OrangeDarkPrimaryContainer, onPrimaryContainer = OrangeDarkOnPrimaryContainer,
    secondary = OrangeDarkSecondary, onSecondary = OrangeDarkOnSecondary,
    secondaryContainer = OrangeDarkSecondaryContainer, onSecondaryContainer = OrangeDarkOnSecondaryContainer,
    tertiary = OrangeDarkTertiary, onTertiary = OrangeDarkOnTertiary,
    tertiaryContainer = OrangeDarkTertiaryContainer, onTertiaryContainer = OrangeDarkOnTertiaryContainer,
    background = OrangeDarkBackground, onBackground = OrangeDarkOnBackground,
    surface = OrangeDarkSurface, onSurface = OrangeDarkOnSurface,
    surfaceVariant = OrangeDarkSurfaceVariant, onSurfaceVariant = OrangeDarkOnSurfaceVariant,
    outline = OrangeDarkOutline, outlineVariant = OrangeDarkOutlineVariant,
    error = OrangeDarkError, onError = OrangeDarkOnError,
    errorContainer = OrangeDarkErrorContainer, onErrorContainer = OrangeDarkOnErrorContainer
)

private val GreenLightColors = lightColorScheme(
    primary = GreenLightPrimary, onPrimary = GreenLightOnPrimary,
    primaryContainer = GreenLightPrimaryContainer, onPrimaryContainer = GreenLightOnPrimaryContainer,
    secondary = GreenLightSecondary, onSecondary = GreenLightOnSecondary,
    secondaryContainer = GreenLightSecondaryContainer, onSecondaryContainer = GreenLightOnSecondaryContainer,
    tertiary = GreenLightTertiary, onTertiary = GreenLightOnTertiary,
    tertiaryContainer = GreenLightTertiaryContainer, onTertiaryContainer = GreenLightOnTertiaryContainer,
    background = GreenLightBackground, onBackground = GreenLightOnBackground,
    surface = GreenLightSurface, onSurface = GreenLightOnSurface,
    surfaceVariant = GreenLightSurfaceVariant, onSurfaceVariant = GreenLightOnSurfaceVariant,
    outline = GreenLightOutline, outlineVariant = GreenLightOutlineVariant,
    error = GreenLightError, onError = GreenLightOnError,
    errorContainer = GreenLightErrorContainer, onErrorContainer = GreenLightOnErrorContainer
)

private val GreenDarkColors = darkColorScheme(
    primary = GreenDarkPrimary, onPrimary = GreenDarkOnPrimary,
    primaryContainer = GreenDarkPrimaryContainer, onPrimaryContainer = GreenDarkOnPrimaryContainer,
    secondary = GreenDarkSecondary, onSecondary = GreenDarkOnSecondary,
    secondaryContainer = GreenDarkSecondaryContainer, onSecondaryContainer = GreenDarkOnSecondaryContainer,
    tertiary = GreenDarkTertiary, onTertiary = GreenDarkOnTertiary,
    tertiaryContainer = GreenDarkTertiaryContainer, onTertiaryContainer = GreenDarkOnTertiaryContainer,
    background = GreenDarkBackground, onBackground = GreenDarkOnBackground,
    surface = GreenDarkSurface, onSurface = GreenDarkOnSurface,
    surfaceVariant = GreenDarkSurfaceVariant, onSurfaceVariant = GreenDarkOnSurfaceVariant,
    outline = GreenDarkOutline, outlineVariant = GreenDarkOutlineVariant,
    error = GreenDarkError, onError = GreenDarkOnError,
    errorContainer = GreenDarkErrorContainer, onErrorContainer = GreenDarkOnErrorContainer
)

private val PurpleLightColors = lightColorScheme(
    primary = PurpleLightPrimary, onPrimary = PurpleLightOnPrimary,
    primaryContainer = PurpleLightPrimaryContainer, onPrimaryContainer = PurpleLightOnPrimaryContainer,
    secondary = PurpleLightSecondary, onSecondary = PurpleLightOnSecondary,
    secondaryContainer = PurpleLightSecondaryContainer, onSecondaryContainer = PurpleLightOnSecondaryContainer,
    tertiary = PurpleLightTertiary, onTertiary = PurpleLightOnTertiary,
    tertiaryContainer = PurpleLightTertiaryContainer, onTertiaryContainer = PurpleLightOnTertiaryContainer,
    background = PurpleLightBackground, onBackground = PurpleLightOnBackground,
    surface = PurpleLightSurface, onSurface = PurpleLightOnSurface,
    surfaceVariant = PurpleLightSurfaceVariant, onSurfaceVariant = PurpleLightOnSurfaceVariant,
    outline = PurpleLightOutline, outlineVariant = PurpleLightOutlineVariant,
    error = PurpleLightError, onError = PurpleLightOnError,
    errorContainer = PurpleLightErrorContainer, onErrorContainer = PurpleLightOnErrorContainer
)

private val PurpleDarkColors = darkColorScheme(
    primary = PurpleDarkPrimary, onPrimary = PurpleDarkOnPrimary,
    primaryContainer = PurpleDarkPrimaryContainer, onPrimaryContainer = PurpleDarkOnPrimaryContainer,
    secondary = PurpleDarkSecondary, onSecondary = PurpleDarkOnSecondary,
    secondaryContainer = PurpleDarkSecondaryContainer, onSecondaryContainer = PurpleDarkOnSecondaryContainer,
    tertiary = PurpleDarkTertiary, onTertiary = PurpleDarkOnTertiary,
    tertiaryContainer = PurpleDarkTertiaryContainer, onTertiaryContainer = PurpleDarkOnTertiaryContainer,
    background = PurpleDarkBackground, onBackground = PurpleDarkOnBackground,
    surface = PurpleDarkSurface, onSurface = PurpleDarkOnSurface,
    surfaceVariant = PurpleDarkSurfaceVariant, onSurfaceVariant = PurpleDarkOnSurfaceVariant,
    outline = PurpleDarkOutline, outlineVariant = PurpleDarkOutlineVariant,
    error = PurpleDarkError, onError = PurpleDarkOnError,
    errorContainer = PurpleDarkErrorContainer, onErrorContainer = PurpleDarkOnErrorContainer
)

@Composable
fun DataWatchTheme(
    themeColor: ThemeColor = ThemeColor.BLUE,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeColor) {
        ThemeColor.BLUE -> if (darkTheme) BlueDarkColors else BlueLightColors
        ThemeColor.ORANGE -> if (darkTheme) OrangeDarkColors else OrangeLightColors
        ThemeColor.GREEN -> if (darkTheme) GreenDarkColors else GreenLightColors
        ThemeColor.PURPLE -> if (darkTheme) PurpleDarkColors else PurpleLightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
