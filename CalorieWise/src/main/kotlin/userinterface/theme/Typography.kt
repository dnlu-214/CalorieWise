package userinterface.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp


val playfulFont = FontFamily(
    Font(
        resource = "font/PlayfulTime-BLBB8.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    )
)

val superBloomFont = FontFamily(
    Font(
        resource = "font/SuperBloom-R9pPV.ttf",
        weight = FontWeight.W400,
        style = FontStyle.Normal
    )
)


val AppTypography = Typography(
    // subtitle2 used as emphasized subtitle
    subtitle2 = TextStyle(
        fontFamily = superBloomFont,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 43.sp,
        letterSpacing = 8.sp
    ),
    h1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = 0.sp
    ),
    // h4 used as app name display
    h4 = TextStyle(
        fontFamily = playfulFont,
        fontWeight = FontWeight.Thin,
        fontSize = 78.sp,
        letterSpacing = 8.sp
    ),
    // h5 used as small subtitle

    // body1 used as notification message prompt
    body1 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp
    ),
    // body2 used as error message prompt
    body2 = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        color = Color.Red
    ),
    button = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        letterSpacing = 1.25.sp
    )
)

