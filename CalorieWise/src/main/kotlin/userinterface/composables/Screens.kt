package userinterface.composables

sealed class Screens(val screen: String) {
    data object Login : Screens("Login")
    data object Homepage : Screens("Homepage")
    data object BasicInfo : Screens("BasicInfo")
    data object Recommendation : Screens("Recommendation")
    data object Records : Screens("Records")
    data object AddFood : Screens("AddFood")
    data object AddDrink : Screens("AddDrink")
    data object AddExercise : Screens("AddExercise")
    data object Analysis : Screens("Analysis")
    data object Settings : Screens("Settings")
}