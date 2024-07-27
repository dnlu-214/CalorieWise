package userinterface.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import userinterface.theme.AppColors

@Composable
fun Appname() {
    val text = "CalorieWise"
    val colors = listOf(
        AppColors.Indigo, AppColors.Teal, AppColors.Sage, AppColors.Pink,
        AppColors.LightViolet, AppColors.Red, AppColors.Violet
    )

    val annotatedString = buildAnnotatedString {
        text.forEachIndexed { index, c ->
            withStyle(style = SpanStyle(color = colors[index % colors.size])) {
                append(c)
            }
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.h4,
        modifier = Modifier.padding(30.dp)
    )
}
