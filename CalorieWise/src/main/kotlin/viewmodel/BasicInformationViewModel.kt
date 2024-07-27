package viewmodel

import DatabaseManager
import androidx.compose.runtime.mutableStateOf
import model.UserModel
import userinterface.ISubscriber
import userinterface.composables.defaultDrinkUnits
import userinterface.composables.updateHeightUnits
import userinterface.composables.updateWeightUnits
import java.sql.Connection
import kotlin.math.roundToInt

class BasicInformationViewModel(val model: UserModel) : ISubscriber {
    var gender = mutableStateOf("")
    var age = mutableStateOf("")
    var height = mutableStateOf("")
    var weight = mutableStateOf("")
    var goalWeight = mutableStateOf("")

    var displayGender = mutableStateOf("")
    var displayAge = mutableStateOf("")
    var displayHeight = mutableStateOf("")
    var displayWeight = mutableStateOf("")
    var displayGoalWeight = mutableStateOf("")

    var calorie = mutableStateOf(0)
    var waterIntake = mutableStateOf(0)
    var exerciseIntake = mutableStateOf(0)
    var fat = mutableStateOf(0)
    var protein = mutableStateOf(0)
    var sugar = mutableStateOf(0)

    var heightUnits = mutableStateOf("")
    var weightUnits = mutableStateOf("")
    var drinkUnits = mutableStateOf("")
    var exerciseUnits = mutableStateOf("")

    var basicInfoMessage = mutableStateOf("")

    init {
        model.subscribe(this)
    }

    //get the latest data from the model.
    override fun update() {
        gender.value = model.gender
        age.value = model.age.toString()
        height.value = model.height.toString()
        weight.value = model.weight.toString()
        goalWeight.value = model.goalWeight.toString()

        calorie.value = model.recommendedCalorieIntake
        waterIntake.value = model.recommendedWaterIntake
        exerciseIntake.value = model.recommendedExerciseIntake
        fat.value = model.recommendedFatIntake
        protein.value = model.recommendedProteinIntake
        sugar.value = model.recommendedSugarIntake

        heightUnits.value = model.heightUnits
        weightUnits.value = model.weightUnits
        drinkUnits.value = model.drinkUnits
        exerciseUnits.value = model.exerciseUnits
    }

    //update the model with information from user input.
    fun updateBasicInformation() {
        model.gender = gender.value
        model.age = age.value.toInt()
        model.height = height.value.toInt()
        model.weight = weight.value.toInt()
        model.goalWeight = goalWeight.value.toInt()

        calculateRecommendation()

        insertOrAddUserBasicInfo(
            model.email, model.height, model.weight, model.goalWeight, model.age,
            gender.value
        )
        model.notifySubscribers()
    }

    fun updateView() {

        model.recommendedCalorieIntake = 0
        model.recommendedWaterIntake = 0
        model.recommendedExerciseIntake = 0
        model.recommendedFatIntake = 0
        model.recommendedProteinIntake = 0
        model.recommendedSugarIntake = 0

        calculateRecommendation()

        displayGender.value = model.gender
        // TODO: why model not initialized as -1
        displayAge.value = if (model.age == 0) "" else model.age.toString()
        displayHeight.value =
            if (model.height == 0) "" else updateHeightUnits(model.height, heightUnits.value).toString()
        displayWeight.value =
            if (model.weight == 0) "" else updateWeightUnits(model.weight, weightUnits.value).toString()
        displayGoalWeight.value =
            if (model.goalWeight == 0) "" else updateWeightUnits(model.goalWeight, weightUnits.value).toString()
    }

    fun checkInput(input: String, value: String): Boolean {
        val result =
            when (input) {
                "sex" -> value == "" || (value == "M" || value == "F" || value == "m" || value == "f")
                "age" -> value == "" || (value.toIntOrNull()?.let { it >= 0 } == true)
                // handle numbers
                else -> value == "" || (value.toDoubleOrNull()?.let { it >= 0.0 } == true)
            }
        return result
    }

    fun calculateRecommendation() {
        calculateCalroieIntake()
        model.recommendedCalorieIntake = calorie.value
        calculateWaterIntake()
        model.recommendedWaterIntake = waterIntake.value
        calculateExercise()
        model.recommendedExerciseIntake = exerciseIntake.value
        calculateFatIntake()
        model.recommendedFatIntake = fat.value
        calculateSugarIntake()
        model.recommendedSugarIntake = sugar.value
        calculateProteinIntake()
        model.recommendedProteinIntake = protein.value
    }

    fun calculateCalroieIntake() {
        //for women: BMR = 655 + (9.6 × body weight in kg) + (1.8 × body height in cm) - (4.7 × age in years);
        // for men: BMR = 66 + (13.7 × weight in kg) + (5 × height in cm) - (6.8 × age in years).
        var calories = 0.0
        val weight = weight.value.toDouble()
        val height = height.value.toDouble()
        val age = age.value.toDouble()
        val exerciseIndex = 1.4625
        if (gender.value == "M" || gender.value == "m") {
            calories = 66 + (13.7 * weight) + (5 * height) - (6.8 * age)
        } else if (gender.value == "F" || gender.value == "f") {
            calories = 655 + (9.6 * weight) + (1.8 * height) - (4.7 * age)
        }
        if (goalWeight.value.toDouble() > weight) {
            calories *= 1.15
        } else {
            calories *= 0.85
        }

        calorie.value = (calories * exerciseIndex).roundToInt()
    }

    fun calculateWaterIntake() {
        //multuply by 67%
//        var water = 0.0
        val weight = weight.value.toDouble()

        // in ounces
        val result = (weight * 0.67)
        waterIntake.value = defaultDrinkUnits(result.toString(), "ounce")
    }

    fun calculateExercise() {
        var time = 0
        val goal = goalWeight.value.toDouble()
        var weight = weight.value.toDouble()
        var age = age.value.toInt()
        if (age in 5..17) {
            time = 60
        } else if (age >= 18) {
            time = 30
        }
        if (goal < weight) {
            time = 40
        }

        exerciseIntake.value = time
    }

    fun calculateFatIntake() {
        val cal = calorie.value.toDouble()

        fat.value = (((cal * 0.275) / 4).roundToInt())
    }

    fun calculateSugarIntake() {
        if (gender.value == "F" || gender.value == "f") {
            sugar.value = 25
        } else {
            sugar.value = 36
        }

    }

    fun calculateProteinIntake() {
        val cal = calorie.value.toDouble()

        protein.value = (((cal * 0.225) / 4).roundToInt())
    }


    private fun Connection.checkUserExistInTable(username: String, tablename: String): Int {
        var exist = -1
        try {
            val query =
                prepareStatement("SELECT COUNT(*) AS user_count FROM ${tablename} WHERE username = '${username}';")
            val result = query.executeQuery()
            result.next()
            exist = result.getInt("user_count")
            result.close()
            query.close()
            return exist
        } catch (exception: Exception) {
            println(exception)
            return -1
        }
    }

    private fun Connection.insertOrUpdateBasicInfo(
        username: String,
        height: Int,
        weight: Int,
        goalWeight: Int,
        age: Int,
        gender: String,
        exist: Int
    ): Int {
        if (exist == 0) {
            try {
                val stmt = createStatement()
                stmt.executeUpdate(
                    "INSERT INTO BasicInfo (username, height, weight, goalWeight, age, gender) " +
                            "VALUES ('${username}', ${height}, ${weight}, ${goalWeight}, ${age}, '${gender}');"
                )
                stmt.close()
                return 1
            } catch (exception: Exception) {
                println(exception)
                return -1
            }
        } else if (exist == 1) {
            try {
                val stmt = createStatement()
                stmt.executeUpdate(
                    "UPDATE BasicInfo SET height = ${height}, weight = ${weight}, " +
                            "goalWeight = ${goalWeight}, age = ${age}, gender = '${gender}' WHERE username = '${username}';"
                )
                stmt.close()
                return 1
            } catch (exception: Exception) {
                println(exception)
                return -1
            }
        } else {
            return -1
        }
    }

    private fun insertOrAddUserBasicInfo(
        /*TODO: add calorie/fat/carbs/protein intake to db too?*/
        username: String,
        height: Int,
        weight: Int,
        goalWeight: Int,
        age: Int,
        gender: String
    ) {
        val databaseManager = DatabaseManager()
        val connection = databaseManager.getConnection()
        val exist = connection.checkUserExistInTable(username, "BasicInfo")
        val insertOrUpdateSuccessCode =
            connection.insertOrUpdateBasicInfo(username, height, weight, goalWeight, age, gender, exist)
        assert(insertOrUpdateSuccessCode == 1)
    }

}



