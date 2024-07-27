package userinterface.composables

import kotlin.math.roundToInt

val heightUnitsList = listOf("centimeters", "meters", "yards", "feet", "inches")
val weightUnitsList = listOf("kilograms", "pounds")
val foodUnitsList = listOf("grams", "pounds", "ounce")
val drinkUnitsList = listOf("milliliters", "liters", "cup", "pint", "quart", "gallon", "ounce")
val exerciseUnitsList = listOf("minutes", "hours")

fun updateHeightUnits(currentAmount: Int, desiredUnits: String): Double {
    // store data as "centimeters"
    val desiredAmount = when (desiredUnits) {
        "centimeters" -> currentAmount.toDouble()
        "meters" -> currentAmount / 100.0
        "yards" -> currentAmount / 91.44
        "feet" -> currentAmount / 30.48
        "inches" -> currentAmount / 2.54
        else -> throw IllegalArgumentException("Unsupported unit: $desiredUnits")
    }
    // Rounding to two decimal places
    return String.format("%.2f", desiredAmount).toDouble()
}

fun defaultHeightUnits(input: String, currentUnit: String): Int {
    // convert the input to centimeters
    val value = input.toDoubleOrNull() ?: return 0
    return when (currentUnit) {
        "centimeters" -> value.toInt()
        "meters" -> (value * 100).toInt()
        "yards" -> (value * 91.44).toInt()
        "feet" -> (value * 30.48).toInt()
        "inches" -> (value * 2.54).toInt()
        else -> 0
    }
}

fun updateWeightUnits(currentAmount: Int, desiredUnits: String): Double {
    // store data as "kilograms"
    val desiredAmount = when (desiredUnits) {
        "kilograms" -> currentAmount.toDouble()
        "pounds" -> currentAmount * 2.205
        else -> throw IllegalArgumentException("Unsupported unit: $desiredUnits")
    }
    // Rounding to two decimal places
    return String.format("%.2f", desiredAmount).toDouble()
}

fun defaultWeightUnits(input: String, currentUnit: String): Int {
    // convert the input to kilograms
    val value = input.toDoubleOrNull() ?: return 0
    return when (currentUnit) {
        "kilograms" -> value.toInt()
        "pounds" -> (value / 2.205).toInt()
        else -> 0
    }
}

fun updateFoodUnits(currentAmount: Int, desiredUnits: String): Double {
    // store data as "grams"
    val desiredAmount = when (desiredUnits) {
        "grams" -> currentAmount.toDouble()
        "pounds" -> currentAmount / 453.6
        "ounce" -> currentAmount / 28.35
        else -> throw IllegalArgumentException("Unsupported unit: $desiredUnits")
    }
    // Rounding to two decimal places
    return String.format("%.2f", desiredAmount).toDouble()
}

fun defaultFoodUnits(input: String, currentUnit: String): Int {
    // convert the input to grams
    val value = input.toDoubleOrNull() ?: return 0
    return when (currentUnit) {
        "grams" -> value.toInt()
        "pounds" -> (value * 453.6).toInt()
        "ounce" -> (value * 28.35).toInt()
        else -> 0
    }
}

fun updateDrinkUnits(currentAmount: Int, desiredUnits: String): Double {
    // store data as "milliliters"
    val desiredAmount = when (desiredUnits) {
        "milliliters" -> currentAmount.toDouble()
        "liters" -> currentAmount / 1000.0
        "cup" -> currentAmount / 236.6
        "pint" -> currentAmount / 473.2
        "quart" -> currentAmount / 946.4
        "gallon" -> currentAmount / 3785.0
        "ounce" -> currentAmount / 29.574
        else -> throw IllegalArgumentException("Unsupported unit: $desiredUnits")
    }
    // Rounding to two decimal places
    return String.format("%.2f", desiredAmount).toDouble()
}

fun defaultDrinkUnits(input: String, currentUnit: String): Int {
    // convert the input to milliliters
    val value = input.toDoubleOrNull() ?: return 0
    return when (currentUnit) {
        "milliliters" -> value.toInt()
        "liters" -> (value * 1000).toInt()
        "cup" -> (value * 236.6).toInt()
        "pint" -> (value * 473.2).toInt()
        "quart" -> (value * 946.4).toInt()
        "gallon" -> (value * 3785).toInt()
        "ounce" -> (value * 29.574).toInt()
        else -> 0
    }
}

fun updateExerciseUnits(currentAmount: Int, desiredUnits: String): Double {
    // store data as "minutes"
    val desiredAmount = when (desiredUnits) {
        "minutes" -> currentAmount.toDouble()
        "hours" -> currentAmount / 60.0
        else -> throw IllegalArgumentException("Unsupported unit: $desiredUnits")
    }
    // Rounding to two decimal places
    return String.format("%.2f", desiredAmount).toDouble()
}

fun defaultExerciseUnits(input: String, currentUnit: String): Int {
    // convert the input to minutes
    val value = input.toDoubleOrNull() ?: return 0
    return when (currentUnit) {
        "minutes" -> value.toInt()
        "hours" -> (value * 60).roundToInt()
        else -> 0
    }
}
