package viewmodel.analysis

import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber

class AnalysisPageViewModel(val model: UserModel) : ISubscriber {
    var fatEaten = mutableStateOf(model.fatTaken)
    var fatIntake = mutableStateOf(model.recommendedFatIntake)

    //another two nutrients
    var sugarIntake = mutableStateOf(model.recommendedSugarIntake)
    var sugarEaten = mutableStateOf(model.sugarTaken)
    var proteinIntake = mutableStateOf(model.recommendedProteinIntake)
    var proteinEaten = mutableStateOf(model.proteinTaken)

    init {
        model.subscribe(this)
    }

    override fun update() {
        fatEaten.value = model.fatTaken
        fatIntake.value = model.recommendedFatIntake
        sugarIntake.value = model.recommendedSugarIntake
        sugarEaten.value = model.sugarTaken
        proteinIntake.value = model.recommendedProteinIntake
        proteinEaten.value = model.proteinTaken
    }
}