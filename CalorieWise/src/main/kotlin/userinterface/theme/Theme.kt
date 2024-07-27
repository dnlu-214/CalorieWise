package userinterface.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = AppColors.Indigo,
    primaryVariant = AppColors.DarkerIndigo,
    onPrimary = AppColors.WhiteSmoke,
    secondary = AppColors.Red,
    secondaryVariant = AppColors.LightViolet,
    error = Color.Red,
    background = AppColors.WhiteSmoke
)

private val DarkColorPalette = darkColors(
    primary = AppColors.LightBlue,
    primaryVariant = Color.LightGray,
    secondary = AppColors.Red,
    secondaryVariant = AppColors.LightViolet,
    onPrimary = AppColors.Dark300,
    error = Color.Red,
    background = AppColors.Dark300
)

@Composable
fun MyApplicationTheme(
    isInDarkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colors = if (isInDarkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
