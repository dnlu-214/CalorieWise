package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AnalysisPageViewModel(val model: UserModel) : ISubscriber {
    var fatEaten = mutableStateOf(model.fatTaken)
    var fatIntake = mutableStateOf(model.recommendedFatIntake)

    //another two nutrients
    var sugarIntake = mutableStateOf(model.recommendedSugarIntake)
    var sugarEaten = mutableStateOf(model.sugarTaken)
    var proteinIntake = mutableStateOf(model.recommendedProteinIntake)
    var proteinEaten = mutableStateOf(model.proteinTaken)

    var weeklyCalorieValue = mutableStateOf( MutableList<Int>(7){0} )
    val currentYearMonth = YearMonth.now()
    val daysInMonth = currentYearMonth.lengthOfMonth()
    var monthlyCalorieValue = mutableStateOf( MutableList<Int>(daysInMonth){0} )

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

    private fun connect(): Connection? {
        var connection: Connection? = null
        try {
            val appDataDir = System.getProperty("user.home") + File.separator + ".CalorieWise"
            val dbPath = "$appDataDir${File.separator}data.db"
            val url = "jdbc:sqlite:$dbPath"

            connection = DriverManager.getConnection(url)
            println("Connection1 is valid.")
        } catch (e: SQLException) {
            println(e.message)
        }
        return connection
    }

    private fun Connection.updateWeeklyCalorieValue(): Int {
        try {
            val today = LocalDate.now() // Get the current date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Define the date format
            for (i in 6 downTo 0) {
                val query = prepareStatement(
                    "SELECT DISTINCT recordType, recordCalorie, recordFat, recordSugar, recordProtein, date FROM Records " +
                            "WHERE username = '${model.email}' AND date = date(?)"
                )
                query.setString(1, today.minusDays(i.toLong()).format(formatter)) // Set the date parameter
                val result = query.executeQuery()
                while (result.next()) {
                    val recordType = result.getString("recordType")
                    val recordCalorie = result.getInt("recordCalorie")
                    if (recordType == "food" || recordType == "drink") {
                        weeklyCalorieValue.value[6-i] += recordCalorie
                    } else {
                        weeklyCalorieValue.value[6-i] -= recordCalorie
                    }
                }
                result.close()
                query.close()
            }
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    fun updateWeeklyCalorieValue() {
        for (i in 0 until weeklyCalorieValue.value.size) {
            weeklyCalorieValue.value[i] = 0
        }
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getWeeklyCalorieValueSuccessCode = connection.updateWeeklyCalorieValue()
        assert(getWeeklyCalorieValueSuccessCode == 1)
    }
    private fun Connection.updateMonthlyCalorieValue(): Int {
        try {
            val today = LocalDate.now() // Get the current date
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Define the date format
            for (i in daysInMonth-1 downTo 0) {
                val query = prepareStatement(
                    "SELECT DISTINCT recordType, recordCalorie, recordFat, recordSugar, recordProtein, date FROM Records " +
                            "WHERE username = '${model.email}' AND date = date(?)"
                )
                query.setString(1, today.minusDays(i.toLong()).format(formatter)) // Set the date parameter
                val result = query.executeQuery()
                while (result.next()) {
                    val recordType = result.getString("recordType")
                    val recordCalorie = result.getInt("recordCalorie")
                    if (recordType == "food" || recordType == "drink") {
                        monthlyCalorieValue.value[daysInMonth-1-i] += recordCalorie
                    } else {
                        monthlyCalorieValue.value[daysInMonth-1-i] -= recordCalorie
                    }
                }
                result.close()
                query.close()
            }
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    fun updateMonthlyCalorieValue() {
        for (i in 0 until monthlyCalorieValue.value.size) {
            monthlyCalorieValue.value[i] = 0
        }
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getMonthlyCalorieValueSuccessCode = connection.updateMonthlyCalorieValue()
        assert(getMonthlyCalorieValueSuccessCode == 1)
    }
}