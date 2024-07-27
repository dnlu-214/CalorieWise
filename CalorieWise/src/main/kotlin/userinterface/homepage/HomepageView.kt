package userinterface.homepage

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import viewmodel.HomepageViewModel

@Composable
fun PieChart(eaten: Int, totalCal: Int, burned: Int, modifier: Modifier = Modifier) {
    val total = totalCal + burned
    val sweepAngle by animateFloatAsState(
        targetValue = if (total > 0) (eaten.toFloat() / total) * 360f else 0f, ////OpenAI. (2024). ChatGPT (March 28 version) [Large language model]. https://chat.openai.com/chat
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearOutSlowInEasing
        )
    )
    val strokeWidth = 40

    Canvas(modifier = modifier.size(300.dp)) {
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

@Composable
fun tracker(eaten: Int, totalCal: Int, burned: Int) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$eaten cal eaten",
                modifier = Modifier.padding(end = 16.dp)
            )
            PieChart(eaten = eaten, totalCal = totalCal, burned = burned)
            Text(
                text = "$burned cal burned",
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        val cal = totalCal - eaten + burned
        if (cal >= 0) {
            Text(
                text = "${cal} cal remaining"
            )
        } else {
            Text(
                text = "${-cal} cal over the daily goal"
            )
        }
    }
}


@Composable
fun HomepageView(
    homepageViewModel: HomepageViewModel,
    onAddFoodClick: () -> Unit, onAddDrinkClick: () -> Unit, onAddExerciseClick: () -> Unit,
) {
    val viewModel by remember { mutableStateOf(homepageViewModel) }
    val eaten = viewModel.calorieEaten.value
    val totalCal = viewModel.calorieIntake.value
    val burned = viewModel.calorieBurned.value
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // Calorie Tracker
        Card(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                Text("Calorie Tracker", style = MaterialTheme.typography.subtitle2)
                tracker(eaten, totalCal, burned)

                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.weight(0.3f).padding(16.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Add Food", style = MaterialTheme.typography.h5)
                            Button(
                                onClick = { onAddFoodClick() },
                                shape = CircleShape
                            ) {
                                Text("+")
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(0.3f).padding(16.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Add Drinks", style = MaterialTheme.typography.h5)
                            Button(
                                onClick = { onAddDrinkClick() },
                                shape = CircleShape
                            ) {
                                Text("+")
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(0.3f).padding(16.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "Add Exercise", style = MaterialTheme.typography.h5)
                            Button(
                                onClick = { onAddExerciseClick() },
                                shape = CircleShape
                            ) {
                                Text("+")
                            }
                        }
                    }
                }
            }
        }
    }
}