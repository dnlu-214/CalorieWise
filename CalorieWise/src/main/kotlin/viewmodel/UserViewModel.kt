package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber
import userinterface.composables.Screens
import java.sql.Connection

class UserViewModel(val model: UserModel) : ISubscriber {
    var isInDarkTheme = mutableStateOf(false)
    var currentScreen = mutableStateOf(Screens.Login.screen)

    init {
        model.subscribe(this)
    }

    override fun update() {
        isInDarkTheme.value = model.isInDarkTheme
    }


    private fun Connection.updateModel(): Int {
        try {
            val query = prepareStatement(
                "SELECT height, weight, gender, goalWeight, age FROM BasicInfo WHERE username = ?;"
            )
            query.setString(1, model.email)

            val result = query.executeQuery()

            if (result.next()) {
                val userHeight = result.getInt("height")
                val userWeight = result.getInt("weight")
                val userGender = result.getString("gender")
                val userGoalWeight = result.getInt("goalWeight")
                val userAge = result.getInt("age")

                model.height = userHeight
                model.weight = userWeight
                model.gender = userGender
                model.goalWeight = userGoalWeight
                model.age = userAge
            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    fun updateModel() {
        model.height = 0
        model.weight = 0
        model.gender = ""
        model.goalWeight = 0
        model.age = 0
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val updateViewSuccessCode = connection.updateModel()
        assert(updateViewSuccessCode == 1)
    }
}