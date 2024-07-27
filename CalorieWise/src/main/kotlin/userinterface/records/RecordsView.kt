package userinterface.records

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import userinterface.composables.*
import viewmodel.RecordsViewModel

@Composable
fun HistoryEntry(
    name: String,
    calorie: String,
    quantity: String,
    favIconPath: String,
    onFavClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier.border(1.dp, Color.Gray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .weight(1f)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = name)
            Text(text = "$calorie cal", color = Color.Gray)
        }
        Text(
            text = quantity,
            modifier = Modifier.align(Alignment.CenterVertically).padding(5.dp)
        )
        Icon(
            painter = painterResource(favIconPath),
            contentDescription = "FavIcon",
            tint = Color.Gray,
            modifier = Modifier
                .padding(5.dp)
                .size(30.dp)
                .clickable { onFavClicked() }
        )
        Icon(
            painter = painterResource("icons/DeleteIcon.png"),
            contentDescription = "DeleteIcon",
            tint = Color.Gray,
            modifier = Modifier
                .padding(5.dp)
                .size(30.dp)
                .clickable { onDeleteClicked() }
        )
    }
}

@Composable
fun RecordsView(
    recordsViewModel: RecordsViewModel,
    overlayVisible: Boolean, recordType: String
) {
    val viewModel by remember { mutableStateOf(recordsViewModel) }
    var overlayVisible by remember { mutableStateOf(overlayVisible) }
    var recordType by remember { mutableStateOf(recordType) }
    var recordItem by remember { mutableStateOf("") }
    var recordAmount by remember { mutableStateOf("") }
    var foodRecords by remember { mutableStateOf(viewModel.foodRecords) }
    var drinkRecords by remember { mutableStateOf(viewModel.drinkRecords) }
    var exerciseRecords by remember { mutableStateOf(viewModel.exerciseRecords) }
    val foodFocusRequester = remember { FocusRequester() }
    val amountFocusRequester = remember { FocusRequester() }
    var showMessagePrompt by remember { mutableStateOf(1) }
    //val focusManager = LocalFocusManager.current

    LaunchedEffect(overlayVisible) {
        if (overlayVisible) {
            foodFocusRequester.requestFocus()
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.weight((1f))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add Food", style = MaterialTheme.typography.h5)
                        Button(
                            onClick = {
                                overlayVisible = true
                                recordType = "food"
                            },
                            shape = CircleShape
                        ) {
                            Text("+")
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight((1f))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add Drinks", style = MaterialTheme.typography.h5)
                        Button(
                            onClick = {
                                overlayVisible = true
                                recordType = "drink"
                            },
                            shape = CircleShape
                        ) {
                            Text("+")
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight((1f))
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Add Exercise", style = MaterialTheme.typography.h5)
                        Button(
                            onClick = {
                                overlayVisible = true
                                recordType = "exercise"
                            },
                            shape = CircleShape
                        ) {
                            Text("+")
                        }
                    }
                }

            }

            // Intake and Workout of the day
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Intake and Workout of the day", style = MaterialTheme.typography.subtitle2)
                    // add details here

                    var deleteTriggered by remember { mutableStateOf(false) }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Card(modifier = Modifier.fillMaxSize().weight(1f)) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(25.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Food", style = MaterialTheme.typography.h5)
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    foodRecords.forEach { record ->
                                        val displayQuantity =
                                            updateFoodUnits(record.second[0], viewModel.foodUnits.value)
                                        var favClicked by remember {
                                            mutableStateOf(
                                                viewModel.getFavourite(
                                                    record.first,
                                                    "food"
                                                )
                                            )
                                        }
                                        HistoryEntry(
                                            record.first,
                                            record.second[1].toString(),
                                            displayQuantity.toString() + " ${viewModel.foodUnits.value}",
                                            if (favClicked) "icons/FavClicked.png" else "icons/FavUnclicked.png",
                                            {
                                                favClicked = !favClicked
                                                viewModel.updateFavourite(record.first, "food", favClicked)
                                            },
                                            {
                                                viewModel.removeRecord(
                                                    item = record.first,
                                                    type = "food",
                                                    amount = record.second[0].toString()
                                                )
                                                deleteTriggered = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Card(modifier = Modifier.fillMaxSize().weight(1f)) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(25.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Drink", style = MaterialTheme.typography.h5)
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    drinkRecords.forEach { record ->
                                        val displayQuantity =
                                            updateDrinkUnits(record.second[0], viewModel.drinkUnits.value)
                                        var favClicked by remember {
                                            mutableStateOf(
                                                viewModel.getFavourite(
                                                    record.first,
                                                    "drink"
                                                )
                                            )
                                        }
                                        HistoryEntry(
                                            record.first,
                                            record.second[1].toString(),
                                            displayQuantity.toString() + " ${viewModel.drinkUnits.value}",
                                            if (favClicked) "icons/FavClicked.png" else "icons/FavUnclicked.png",
                                            {
                                                favClicked = !favClicked
                                                viewModel.updateFavourite(record.first, "drink", favClicked)
                                            },
                                            {
                                                viewModel.removeRecord(
                                                    item = record.first,
                                                    type = "drink",
                                                    amount = record.second[0].toString()
                                                )
                                                deleteTriggered = true
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        Card(modifier = Modifier.fillMaxSize().weight(1f)) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(25.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Exercise", style = MaterialTheme.typography.h5)
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.verticalScroll(rememberScrollState())
                                ) {
                                    exerciseRecords.forEach { record ->
                                        val displayQuantity =
                                            updateExerciseUnits(record.second[0].toInt(), viewModel.exerciseUnits.value)
                                        var favClicked by remember {
                                            mutableStateOf(
                                                viewModel.getFavourite(
                                                    record.first,
                                                    "exercise"
                                                )
                                            )
                                        }
                                        HistoryEntry(
                                            record.first,
                                            record.second[1].toString(),
                                            displayQuantity.toString() + " ${viewModel.exerciseUnits.value}",
                                            if (favClicked) "icons/FavClicked.png" else "icons/FavUnclicked.png",
                                            {
                                                favClicked = !favClicked
                                                viewModel.updateFavourite(record.first, "exercise", favClicked)
                                            },
                                            {
                                                viewModel.removeRecord(
                                                    item = record.first,
                                                    type = "exercise",
                                                    amount = record.second[0].toString()
                                                )
                                                deleteTriggered = true
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        if (deleteTriggered) {
                            recordsViewModel.updateView()
                            deleteTriggered = false
                        }
                    }
                }
            }
        }

        if (viewModel.showMessagePrompt.value) {
            MessagePrompt(viewModel.recordsMessage.value, { viewModel.showMessagePrompt.value = false }, "message")
        }

        // Overlay area
        if (overlayVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(MaterialTheme.colors.background.copy(alpha = 0.8f), shape = RectangleShape),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                ) {
                    val handleEnterEvent: (KeyEvent) -> Boolean = { keyEvent ->
                        if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                            showMessagePrompt = recordsViewModel.addRecord(recordItem, recordAmount, recordType)
                            recordItem = ""
                            recordAmount = ""
                            overlayVisible = false
                            true
                        } else {
                            false
                        }
                    }
                    Column(
                        modifier = Modifier.padding(16.dp).onKeyEvent(handleEnterEvent)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {

                            var expanded by remember { mutableStateOf(false) }
                            var isRecent by remember { mutableStateOf(false) }
                            // var lastKeyWasTab by remember { mutableStateOf(false) }

                            Box(modifier = Modifier.width(550.dp)) {
                                TextField(
                                    value = recordItem,
                                    onValueChange = { recordItem = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
//                                        .onGloballyPositioned { coordinates ->
//                                            //This value is used to assign to the DropDown the same width
//                                            textfieldSize = coordinates.size.toSize()
//                                        },

                                        .focusRequester(foodFocusRequester)
                                        .onKeyEvent { keyEvent ->
                                            if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyUp) {
                                                recordItem = recordItem.trimEnd()
                                                amountFocusRequester.requestFocus()
                                                true
                                            } else {
                                                false
                                            }
                                        },


                                    label = {
                                        var inputTypePrompt =
                                            when (recordType) {
                                                "food" -> "food you consumed"
                                                "drink" -> "drink you consumed"
                                                "exercise" -> "exercise you performed"
                                                else -> assert(false)
                                            }
                                        Text("Please enter the name of the $inputTypePrompt")
                                    },
                                    trailingIcon = {
                                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                            Icon(
                                                painter = painterResource("icons/Recent.png"),
                                                contentDescription = "View",
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clickable {
                                                        expanded = !expanded
                                                        isRecent = true
                                                    }
                                            )
                                            Icon(
                                                painter = painterResource("icons/FavClicked.png"),
                                                contentDescription = "View",
                                                tint = Color.Gray,
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clickable {
                                                        expanded = !expanded
                                                        isRecent = false
                                                    }
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                        }
                                    }
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    var suggestions = viewModel.getSuggestionList(recordType, isRecent)
                                    if (suggestions.isEmpty()) {
                                        suggestions = if (isRecent) listOf("Uh-oh, nothing has been recently added!")
                                        else listOf("Uh-oh, nothing has been added to Favourite!")
                                    }
                                    suggestions.forEach { label ->
                                        DropdownMenuItem(onClick = {
                                            recordItem = label
                                        }) {
                                            Text(text = label)
                                        }
                                    }
                                }
                            }

                            var displayAmount by remember { mutableStateOf("") }

                            TextField(
                                value = displayAmount,
                                onValueChange = {
                                    displayAmount = it.trim()
                                    recordAmount =
                                        when (recordType) {
                                            "food" -> defaultFoodUnits(
                                                displayAmount,
                                                viewModel.foodUnits.value
                                            ).toString()

                                            "drink" -> defaultDrinkUnits(
                                                displayAmount,
                                                viewModel.drinkUnits.value
                                            ).toString()

                                            "exercise" -> defaultExerciseUnits(
                                                displayAmount,
                                                viewModel.exerciseUnits.value
                                            ).toString()

                                            else -> assert(false).toString()
                                        }
                                },
                                label = {
                                    var inputAmountPrompt =
                                        when (recordType) {
                                            "food" -> "Amount (${viewModel.foodUnits.value})"
                                            "drink" -> "Amount (${viewModel.drinkUnits.value})"
                                            "exercise" -> "Duration (${viewModel.exerciseUnits.value})"
                                            else -> assert(false)
                                        }
                                    Text("$inputAmountPrompt")
                                },
                                modifier = Modifier.width(200.dp)
                                    .focusRequester(amountFocusRequester)
                                    .onKeyEvent { keyEvent ->
                                        if (keyEvent.key == Key.Tab && keyEvent.type == KeyEventType.KeyUp) {
                                            foodFocusRequester.requestFocus()
                                            true
                                        } else false
                                    }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Button(
                                onClick = {
                                    showMessagePrompt = recordsViewModel.addRecord(recordItem, recordAmount, recordType)
                                    recordItem = ""
                                    recordAmount = ""
                                    overlayVisible = false
                                },
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text("Add")
                            }
                            Button(
                                onClick = {
                                    recordItem = ""
                                    recordAmount = ""
                                    overlayVisible = false
                                },
                                modifier = Modifier.width(100.dp)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
        if (showMessagePrompt == 0) {
            MessagePrompt(
                "Oops, this $recordType is not recognized, please check your spelling and try entering your $recordType again. ",
                { showMessagePrompt = 1 },
                "message"
            )
        }
    }
}