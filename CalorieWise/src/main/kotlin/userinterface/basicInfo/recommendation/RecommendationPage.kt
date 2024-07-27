package userinterface.basicInfo.recommendation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import userinterface.composables.updateDrinkUnits
import userinterface.composables.updateExerciseUnits
import viewmodel.BasicInformationViewModel

@Composable
fun RecommendationPage(
    basicInformationViewModel: BasicInformationViewModel,
    onNextStepClick: () -> Unit
) {
    val viewModel by remember { mutableStateOf(basicInformationViewModel) }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CalorieWise RECOMMENDS: ",
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.padding(30.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(35.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                recommendationRow("Food", "Calorie Intake", "${viewModel.calorie.value} Cals")

                val displayDrink = updateDrinkUnits(viewModel.waterIntake.value, viewModel.drinkUnits.value)
                recommendationRow("Drink", "Water Intake", "${displayDrink} ${viewModel.drinkUnits.value}")

                val displayExercise = updateExerciseUnits(viewModel.exerciseIntake.value, viewModel.exerciseUnits.value)
                recommendationRow("Exercise", "Exercise", "${displayExercise} ${viewModel.exerciseUnits.value}")

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onNextStepClick() },
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Center the button within the column
                ) {
                    Text("Next Step")
                }
            }
        }
    }
}

@Composable
fun recommendationRow(type: String, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource("icons/${type}Icon.png"),
            contentDescription = null,
            modifier = Modifier.size(width = 59.dp, height = 59.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = label,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primaryVariant),
            modifier = Modifier.padding(10.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.secondary),
            modifier = Modifier.padding(10.dp)
        )
        Text(
            text = "/day",
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.primaryVariant),
            modifier = Modifier.padding(5.dp)
        )
    }
}
