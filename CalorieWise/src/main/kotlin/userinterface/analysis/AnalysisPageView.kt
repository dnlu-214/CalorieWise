package userinterface.analysis

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import viewmodel.AnalysisPageViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun PieChart(eaten: Int, total: Int, modifier: Modifier = Modifier) {
    val sweepAngle by animateFloatAsState( ////OpenAI. (2024). ChatGPT (March 28 version) [Large language model]. https://chat.openai.com/chat
        targetValue = if (total > 0) (eaten.toFloat() / total) * 360f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        )
    )
    val strokeWidth = 20

    Canvas(modifier = modifier.size(250.dp)) {
        drawArc(
            color = Color.Red,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toFloat())
        )

        drawArc(
            color = Color.LightGray,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(width = strokeWidth.toFloat(), cap = StrokeCap.Round)
        )
    }
}

data class Point(val x: Float, val y: Float)

@Composable
fun TrendGraph(calorieValues: MutableList<Int>, type: String) {
    val calorieSize = calorieValues.size
    val caloriePoints = MutableList<Point>(calorieSize) { Point(0f, 0f) }
    for (i in 0 until calorieSize) {
        caloriePoints[i] = Point((i + 0.5).toFloat(), calorieValues[i].toFloat())
    }

    var xAxisLabels = mutableListOf<String>()
    val today = LocalDate.now()
    val start = today.minusDays((calorieSize - 1).toLong())
    var currentDate = start
    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    if (type == "Month") formatter = DateTimeFormatter.ofPattern("dd")

    while (currentDate <= today) {
        xAxisLabels.add(currentDate.format(formatter))
        currentDate = currentDate.plusDays(1)
    }

    // find max and min value of X, we will need that later
    val minXValue = 0f
    val maxXValue = calorieSize.toFloat()

    // find max and min value of Y, we will need that later
    val minYValue = 0f
    val maxYValue = caloriePoints.maxOf { it.y }

    val valPerSlot = ((((calorieValues.maxOrNull() ?: 1) / (15 * 10)).toDouble().roundToInt() * 10).coerceAtLeast(10))

    val axisColor = MaterialTheme.colors.primaryVariant
    val lineColor = MaterialTheme.colors.secondary

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row {
            Column(
                modifier = Modifier.height(530.dp).offset(y = -10.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.End
            ) {
                var yLabel = mutableListOf<Int>()
                for (i in 0 until (calorieValues.maxOrNull() ?: 1) step valPerSlot) {
                    yLabel.add(i)
                }
                yLabel.reverse()
                for (label in yLabel) {
                    Text(label.toString())
                }
            }
            Spacer(modifier = Modifier.width(15.dp))

            Column(modifier = Modifier.width(1020.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.size(1000.dp, 500.dp)) {
                    drawLine(
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        color = axisColor,
                        strokeWidth = 2f
                    )

                    // Draw Y-axis
                    drawLine(
                        start = Offset(0f, size.height),
                        end = Offset(0f, 0f),
                        color = axisColor,
                        strokeWidth = 2f
                    )

                    // Draw labels on X-axis
                    val xStep = size.width / calorieSize
                    for (index in 0 until calorieSize) {
                        drawLine(
                            start = Offset(xStep / 2 + index * xStep, size.height),
                            end = Offset(xStep / 2 + index * xStep, size.height + 10.dp.toPx()),
                            color = axisColor,
                            strokeWidth = 2f
                        )
                    }

                    // Draw labels on Y-axis
                    val yStep = size.height / ((calorieValues.maxOrNull() ?: 1))
                    for (i in 0 until (calorieValues.maxOrNull() ?: 1) step valPerSlot) {
                        drawLine(
                            start = Offset(0f, size.height - i * yStep),
                            end = Offset(-10.dp.toPx(), size.height - i * yStep),
                            color = axisColor,
                            strokeWidth = 2f
                        )
                    }

                    // map data points to pixel values, in canvas we think in pixels
                    val pixelPoints = caloriePoints.map {
                        // we use extension function to convert and scale initial values to pixels
                        val x = it.x.mapValueToDifferentRange(
                            inMin = minXValue,
                            inMax = maxXValue,
                            outMin = 0f,
                            outMax = size.width
                        )

                        // same with y axis
                        val y = it.y.mapValueToDifferentRange(
                            inMin = minYValue,
                            inMax = maxYValue,
                            outMin = size.height,
                            outMax = 0f
                        )

                        Point(x, y)
                    }

                    val path = Path() // prepare path to draw

                    // in the loop below we fill our path
                    pixelPoints.forEachIndexed { index, point ->
                        if (index == 0) { // for the first point we just move drawing cursor to the position
                            path.moveTo(point.x, point.y)
                        } else {
                            path.lineTo(point.x, point.y) // for rest of points we draw the line
                        }
                    }

                    // and finally we draw the path
                    drawPath(
                        path,
                        color = lineColor,
                        style = Stroke(width = 3f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.width(1020.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    for (date in xAxisLabels) {
                        Text(date)
                    }
                }
            }
        }
    }

}

// simple extension function that allows conversion between ranges
fun Float.mapValueToDifferentRange(
    inMin: Float,
    inMax: Float,
    outMin: Float,
    outMax: Float
) = (this - inMin) * (outMax - outMin) / (inMax - inMin) + outMin


@Composable
fun AnalysisPageView(analysisPageViewModel: AnalysisPageViewModel) {
    // val viewModel by remember { mutableStateOf(analysisPageViewModel) }
    val selectedButton = remember { mutableStateOf("Today") }
    val fatTotal = analysisPageViewModel.fatIntake.value
    val fatEaten = analysisPageViewModel.fatEaten.value
    //also carbs and protein
    val sugarTotal = analysisPageViewModel.sugarIntake.value
    val sugarEaten = analysisPageViewModel.sugarEaten.value
    val proteinTotal = analysisPageViewModel.proteinIntake.value
    val proteinEaten = analysisPageViewModel.proteinEaten.value


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Nutrients Board
        Card(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Nutrients Board", style = MaterialTheme.typography.subtitle2)

                Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(
                        onClick = { selectedButton.value = "Today" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedButton.value == "Today") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Today")
                    }
                    Button(
                        onClick = { selectedButton.value = "Week" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedButton.value == "Week") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Week")
                    }
                    Button(
                        onClick = { selectedButton.value = "Month" },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (selectedButton.value == "Month") MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
                        )
                    ) {
                        Text("Month")
                    }
                    Modifier.padding(bottom = 20.dp)
                }
                if (selectedButton.value == "Today") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(50.dp),

                        ) {
                        Box(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PieChart(fatEaten, fatTotal)
                            val percentage = if (fatTotal > 0) {
                                (fatEaten.toFloat() / fatTotal * 100).roundToInt()
                            } else {
                                '0'
                            }
                            Text(text = "$percentage% of recommended \n Fats consumed")
                        }
                        //another two boxes for two charts
                        Box(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PieChart(sugarEaten, sugarTotal)//change parameters
                            val percentage = if (sugarTotal > 0) {
                                (sugarEaten.toFloat() / sugarTotal * 100).roundToInt()
                            } else {
                                '0'
                            }
                            Text(text = "$percentage% of recommended \n Sugar consumed")
                        }
                        Box(
                            modifier = Modifier.weight(1f).padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            PieChart(proteinEaten, proteinTotal)//change
                            val percentage = if (proteinTotal > 0) {
                                (proteinEaten.toFloat() / proteinTotal * 100).roundToInt()
                            } else {
                                '0'
                            }
                            Text(text = "$percentage% of recommended \n Protein consumed")
                        }

                    }
                } else if (selectedButton.value == "Week") {
                    analysisPageViewModel.updateWeeklyCalorieValue()
                    TrendGraph(analysisPageViewModel.weeklyCalorieValue.value, "Week")
                } else if (selectedButton.value == "Month") {
                    analysisPageViewModel.updateMonthlyCalorieValue()
                    TrendGraph(analysisPageViewModel.monthlyCalorieValue.value, "Month")
                }
            }
        }
    }
}
