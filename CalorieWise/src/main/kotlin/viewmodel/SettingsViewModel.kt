package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber
import userinterface.settings.SettingsViewEvent
import java.sql.Connection

class SettingsViewModel(val model: UserModel) : ISubscriber {

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    var loggedin = true
    var passwordEdited = false
    var settingsMessage = mutableStateOf("")
    var isInDarkTheme = mutableStateOf(false)
    var heightUnits = mutableStateOf("")
    var weightUnits = mutableStateOf("")
    var foodUnits = mutableStateOf("")
    var drinkUnits = mutableStateOf("")
    var exerciseUnits = mutableStateOf("")

    val FavouriteList: MutableList<String> = mutableListOf()
    var recordFav = false

    init {
        model.subscribe(this)
        isInDarkTheme.value = model.isInDarkTheme
    }

    private fun signOut() {
        model.loggedIn = false

        // Clear session data
        model.email = ""
        model.password = ""
        model.gender = ""
        model.age = -1
        model.height = -1
        model.weight = -1
        model.goalWeight = -1
        model.recommendedCalorieIntake = 0
        model.recommendedWaterIntake = 0
        model.recommendedExerciseIntake = 0
        model.recommendedFatIntake = 0
        model.recommendedProteinIntake = 0
        model.recommendedSugarIntake = 0
        model.calorieTaken = 0
        model.calorieBurned = 0
        model.waterTaken = 0
        model.fatTaken = 0
        model.proteinTaken = 0
        model.sugarTaken = 0
        println("Data cleared")
    }


    private fun Connection.updatePassword(
        newPassword: String
    ): Int {
        try {
            val stmt = createStatement()
            stmt.executeUpdate(
                "UPDATE Users SET password = '${newPassword}' WHERE username = '${model.email}';"
            )
            stmt.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun updatePassword(newPassword: String) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val updatePasswordSuccessCode = connection.updatePassword(newPassword)
        assert(updatePasswordSuccessCode == 1)
        model.password = newPassword
        settingsMessage.value = "You've changed your password! "
        model.notifySubscribers()
    }

    private fun updateUnits(type: String, units: String) {
        when (type) {
            "Height" -> model.heightUnits = units
            "Weight" -> model.weightUnits = units
            "Food" -> model.foodUnits = units
            "Drink" -> model.drinkUnits = units
            "Exercise" -> model.exerciseUnits = units
            else -> assert(false)
        }
    }

    private fun Connection.getFavouriteList(recordType: String): Int {
        try {
            val query = prepareStatement(
                "SELECT DISTINCT recordItem, recordType, favourite FROM Records " +
                        "WHERE username = '${model.email}' AND recordType = '${recordType}' AND favourite = '${1}';"
            )
            val result = query.executeQuery()
            while (result.next()) {
                val resultItem = result.getString("recordItem")
                FavouriteList.add(resultItem)
            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    fun getFavouriteList(recordType: String) {
        FavouriteList.clear()
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getSuggestionListSuccessCode = connection.getFavouriteList(recordType)
        assert(getSuggestionListSuccessCode == 1)
    }

    private fun Connection.updateFavourite(recordItem: String, recordType: String, favClicked: Boolean): Int {
        try {
            val stmt = createStatement()
            stmt.executeUpdate(
                "UPDATE Records SET favourite = ${if (favClicked) 1 else 0} " +
                        "WHERE username = '${model.email}' AND recordItem = '${recordItem}' AND recordType = '${recordType}';"
            )
            stmt.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun Connection.getFavourite(recordItem: String, recordType: String): Int {
        try {
            val query = prepareStatement(
                "SELECT favourite FROM Records " +
                        "WHERE username = '${model.email}' AND recordItem = '${recordItem}' AND recordType = '${recordType}'"
            )
            val result = query.executeQuery()
            while (result.next()) {
                recordFav = result.getBoolean("favourite")
            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    fun updateFavourite(recordItem: String, recordType: String, favClicked: Boolean) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getUpdateSuccessCode = connection.updateFavourite(recordItem, recordType, favClicked)
        assert(getUpdateSuccessCode == 1)
    }

    fun getFavourite(recordItem: String, recordType: String): Boolean {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getFavouriteSuccessCode = connection.getFavourite(recordItem, recordType)
        assert(getFavouriteSuccessCode == 1)
        return recordFav
    }

    fun invoke(event: SettingsViewEvent, value1: Any?, value2: Any?) {
        when (event) {
            SettingsViewEvent.SignOutEvent -> signOut()
            SettingsViewEvent.ChangePasswordEvent -> updatePassword(value1 as String)
            SettingsViewEvent.ChangeThemeEvent -> model.isInDarkTheme = !model.isInDarkTheme
            SettingsViewEvent.UnitsConversionEvent -> updateUnits(value1 as String, value2 as String)
            SettingsViewEvent.FavoritesEditingEvent -> getFavouriteList(value1 as String)
        }
    }

    override fun update() {
        email.value = model.email
        password.value = model.password
        loggedin = model.loggedIn
        isInDarkTheme.value = model.isInDarkTheme
        heightUnits.value = model.heightUnits
        weightUnits.value = model.weightUnits
        foodUnits.value = model.foodUnits
        drinkUnits.value = model.drinkUnits
        exerciseUnits.value = model.exerciseUnits
    }
}