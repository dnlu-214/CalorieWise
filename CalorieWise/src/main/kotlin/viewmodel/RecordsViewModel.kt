package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import model.UserModel
import userinterface.ISubscriber
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.sql.Connection


class RecordsViewModel(val model: UserModel) : ISubscriber {
    val foodRecords: MutableList<Pair<String, List<Int>>> = mutableListOf()
    val drinkRecords: MutableList<Pair<String, List<Int>>> = mutableListOf()
    val exerciseRecords: MutableList<Pair<String, List<Int>>> = mutableListOf()

    var foodUnits = mutableStateOf("")
    var drinkUnits = mutableStateOf("")
    var exerciseUnits = mutableStateOf("")

    val suggestionList: MutableList<String> = mutableListOf()
    var recordFav = false

    var showMessagePrompt = mutableStateOf(false)
    var recordsMessage = mutableStateOf("")

    init {
        model.subscribe(this)
    }

    override fun update() {
        foodUnits.value = model.foodUnits
        drinkUnits.value = model.drinkUnits
        exerciseUnits.value = model.exerciseUnits
    }

    fun addRecord(item: String, amount: String, type: String): Int {
        val nutritionInfo = calculateCalorieofNewIntake(item, amount, type)
        val calorie = nutritionInfo[0]
        val fat = nutritionInfo[1]
        val protein = nutritionInfo[2]
        val sugar = nutritionInfo[3]
        if (calorie != -1) {
            insertRecord(item, amount, type, calorie, fat, protein, sugar)
            updateView()
            model.notifySubscribers()
            return 1
        } else {
            return 0
        }
    }

    fun removeRecord(item: String, amount: String, type: String) {
        val nutritionInfo = calculateCalorieofNewIntake(item, amount, type)
        val calorie = nutritionInfo[0]
        val fat = nutritionInfo[1]
        val protein = nutritionInfo[2]
        val sugar = nutritionInfo[3]
        deleteRecord(item, amount, type, calorie, fat, protein, sugar)
        updateView()
        model.notifySubscribers()
    }

    private fun Connection.insertRecord(
        username: String,
        item: String,
        amount: Int,
        type: String,
        calorie: Int, fat: Int, protein: Int, sugar: Int
    ): Int {
        try {
            var exist = -1
            val query =
                prepareStatement(
                    "SELECT COUNT(*) AS record_count FROM Records " +
                            "WHERE username = '${username}' AND recordItem = '${item}' AND recordType = '${type}' AND date = CURDATE();"
                )
            val result = query.executeQuery()
            result.next()
            exist = result.getInt("record_count")
            result.close()
            query.close()

            val stmt = createStatement()
            if (exist > 0) {

                stmt.executeUpdate(
                    "UPDATE Records " +
                            "SET recordAmount = recordAmount + ${amount}, recordCalorie = recordCalorie + ${calorie}, " +
                            "recordFat = recordFat + ${fat}, recordProtein = recordProtein + ${protein}, recordSugar = recordSugar + ${sugar} " +
                            "WHERE username = '${model.email}' AND recordItem = '${item}' AND recordType = '${type}' AND date = CURDATE()"
                )

                showMessagePrompt.value = true
                when (type) {
                    "exercise" -> recordsMessage.value = "Duration of '${item}' has been updated."
                    else -> recordsMessage.value = "Amount of '${item}' has been updated."
                }
            } else {
                stmt.executeUpdate(
                    "INSERT INTO Records (username, recordItem, recordType, recordAmount, date, recordCalorie, recordFat, recordProtein, recordSugar, favourite) " +
                            "VALUES ('${username}', '${item}', '${type}', ${amount}, CURDATE(), ${calorie}, ${fat}, ${protein}, ${sugar}, ${0});"
                )
            }
            stmt.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun Connection.deleteRecord(
        username: String,
        item: String,
        amount: Int,
        type: String,
        calorie: Int, fat: Int, protein: Int, sugar: Int
    ): Int {
        try {
            var exist = -1
            val query =
                prepareStatement(
                    "SELECT COUNT(*) AS record_count FROM Records " +
                            "WHERE username = '${username}' AND recordItem = '${item}' AND recordType = '${type}' AND date = CURDATE();"
                )
            val result = query.executeQuery()
            result.next()
            exist = result.getInt("record_count")
            result.close()
            query.close()

            val stmt = createStatement()
            if (exist > 0) {
                stmt.executeUpdate(
                    "DELETE FROM Records WHERE username = '${model.email}'" +
                            " AND recordItem = '${item}' AND recordType = '${type}' AND date = CURDATE();"
                )
            }
            stmt.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }


    private fun Connection.updateView(): Int {
        try {
            val query = prepareStatement(
                "SELECT recordItem, recordAmount, recordType, date, recordCalorie, recordFat, recordProtein, recordSugar " +
                        "FROM Records WHERE username = '${model.email}' AND date = CURDATE();"
            )
            val result = query.executeQuery()
            while (result.next()) {
                val resultType = result.getString("recordType")
                val resultItem = result.getString("recordItem")
                val resultAmount = result.getInt("recordAmount")
                val resultCalorie = result.getInt("recordCalorie")
                val resultProtein = result.getInt("recordProtein")
                val resultSugar = result.getInt("recordSugar")
                val resultFat = result.getInt("recordFat")
                when (resultType) {
                    "food" -> {
                        foodRecords.add(Pair(resultItem, listOf(resultAmount, resultCalorie)))
                        model.calorieTaken += resultCalorie
                        model.fatTaken += resultFat
                        model.proteinTaken += resultProtein
                        model.sugarTaken += resultSugar
                    }

                    "drink" -> {
                        drinkRecords.add(Pair(resultItem, listOf(resultAmount, resultCalorie)))
                        model.calorieTaken += resultCalorie
                        model.fatTaken += resultFat
                        model.proteinTaken += resultProtein
                        model.sugarTaken += resultSugar
                    }

                    "exercise" -> {
                        exerciseRecords.add(Pair(resultItem, listOf(resultAmount, resultCalorie)))
                        model.calorieBurned += resultCalorie
                    }

                    else -> assert(false)
                }

            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun Connection.getSuggestionList(recordType: String, isRecent: Boolean): Int {
        try {
            val query = if (isRecent) prepareStatement(
                "SELECT DISTINCT recordItem, recordType FROM Records " +
                        "WHERE username = '${model.email}' AND recordType = '${recordType}' ORDER BY ROWID DESC LIMIT 5;"
            )
            else prepareStatement(
                "SELECT DISTINCT recordItem, recordType, favourite FROM Records " +
                        "WHERE username = '${model.email}' AND recordType = '${recordType}' AND favourite = '${1}';"
            )
            val result = query.executeQuery()
            while (result.next()) {
                val resultItem = result.getString("recordItem")
                suggestionList.add(resultItem)
            }
            result.close()
            query.close()
            return 1
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
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

    private fun insertRecord(
        item: String,
        amount: String,
        type: String,
        calorie: Int,
        fat: Int,
        protein: Int,
        sugar: Int
    ) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val insertSuccessCode =
            connection.insertRecord(model.email, item, amount.toInt(), type, calorie, fat, protein, sugar)
        assert(insertSuccessCode == 1)
    }

    private fun deleteRecord(
        item: String,
        amount: String,
        type: String,
        calorie: Int,
        fat: Int,
        protein: Int,
        sugar: Int
    ) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val deleteSuccessCode =
            connection.deleteRecord(model.email, item, amount.toInt(), type, calorie, fat, protein, sugar)
        assert(deleteSuccessCode == 1)
    }

    private fun calculateCalorieofNewIntake(item: String, amount: String, type: String): List<Int> {
        var calorie: Int = 0
        var fat: Int = 0
        var protein: Int = 0
        var sugar: Int = 0
        val newAmount = amount.replace(" ", "%20")
        val newItem = item.replace(" ", "%20")
        val apikey = "Z42q0ajL9oxbsMkdlrIylA==a3w338OixwhNmIEt"
        val uri = if (type == "food" || type == "drink") {
            URI("https://api.api-ninjas.com/v1/nutrition?x-api-key=${apikey}&query=${newAmount}g%20${newItem}")
        } else {
            URI("https://api.api-ninjas.com/v1/caloriesburned?x-api-key=${apikey}&activity=${newItem}&duration=${newAmount}")
        }
        val url = uri.toURL()
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("accept", "application/json")
        val responseStream: InputStream = connection.inputStream
        val mapper = ObjectMapper()
        val root: JsonNode = mapper.readTree(responseStream)
        if (type == "food" || type == "drink") {
            if (root.size() == 0 || root.isNull) {
                calorie = -1
            } else {
                calorie = root[0]["calories"].asInt()
                fat = root[0]["fat_total_g"].asInt()
                protein = root[0]["protein_g"].asInt()
                sugar = root[0]["sugar_g"].asInt()
            }
            model.fatTaken += fat
            model.proteinTaken += protein
            model.sugarTaken += sugar
            model.calorieTaken += calorie
        } else {
            if (root.size() == 0 || root.isNull) {
                calorie = -1
            } else {
                calorie = root[0]["total_calories"].asInt()
            }
            model.calorieBurned += calorie
        }
        return listOf(calorie, fat, protein, sugar)
    }

    fun updateView() {
        foodRecords.clear()
        drinkRecords.clear()
        exerciseRecords.clear()
        model.calorieBurned = 0
        model.fatTaken = 0
        model.calorieTaken = 0
        model.proteinTaken = 0
        model.sugarTaken = 0
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val updateViewSuccessCode = connection.updateView()
        assert(updateViewSuccessCode == 1)
    }

    fun getSuggestionList(recordType: String, isRecent: Boolean): List<String> {
        suggestionList.clear()
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val getSuggestionListSuccessCode = connection.getSuggestionList(recordType, isRecent)
        assert(getSuggestionListSuccessCode == 1)
        return suggestionList
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
}