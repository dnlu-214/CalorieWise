package viewmodel.homepage

import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber

class HomepageViewModel(val model: UserModel) : ISubscriber {
    var calorieEaten = mutableStateOf(0)
    var calorieIntake = mutableStateOf(0)
    var calorieBurned = mutableStateOf(0)

    init {
        model.subscribe(this)
    }

    override fun update() {
        calorieEaten.value = model.calorieTaken
        calorieIntake.value = model.recommendedCalorieIntake
        calorieBurned.value = model.calorieBurned
    }
}