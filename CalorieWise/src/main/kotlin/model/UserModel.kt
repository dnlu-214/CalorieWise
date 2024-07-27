package model

class UserModel : IPublisher() {
    var firstname: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }

    var lastname: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }

    var email: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }

    var password: String = ""
        set(value) {
            field = value
            notifySubscribers()
        }
    var gender: String = ""

    var age: Int = -1

    var height: Int = -1

    var weight: Int = -1

    var goalWeight: Int = -1
    var recommendedCalorieIntake: Int = 0
    var recommendedWaterIntake: Int = 0
    var recommendedExerciseIntake: Int = 0
    var recommendedFatIntake: Int = 0
    var recommendedProteinIntake: Int = 0
    var recommendedSugarIntake: Int = 0


    var loggedIn: Boolean = false
        set(value) {
            field = value
            notifySubscribers()
        }

    var calorieTaken: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    var calorieBurned: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    var waterTaken: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    var fatTaken: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    var proteinTaken: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    var sugarTaken: Int = 0
        set(value) {
            field = value
            notifySubscribers()
        }

    // Remember to clear newly added data in SettingsViewModel.kt/signOut()

    var isInDarkTheme: Boolean = false
        set(value) {
            field = value
            notifySubscribers()
        }

    var heightUnits: String = "centimeters"
        set(value) {
            field = value
            notifySubscribers()
        }

    var weightUnits: String = "kilograms"
        set(value) {
            field = value
            notifySubscribers()
        }

    var foodUnits: String = "grams"
        set(value) {
            field = value
            notifySubscribers()
        }

    var drinkUnits: String = "milliliters"
        set(value) {
            field = value
            notifySubscribers()
        }

    var exerciseUnits: String = "minutes"
        set(value) {
            field = value
            notifySubscribers()
        }
}

